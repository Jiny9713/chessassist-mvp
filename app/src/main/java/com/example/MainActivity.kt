package com.example

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.content.ContextCompat
import android.Manifest
import com.example.capture.CaptureManager
import com.example.api.ChessvisionRepository
import com.example.api.StockfishRepository
import com.example.overlay.startOverlayService
import com.example.overlay.stopOverlayService
import com.example.overlay.updateOverlayText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main activity that ties together screen capture, FEN extraction, Stockfish
 * analysis and overlay updates. The flow repeats on a fixed interval.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var captureManager: CaptureManager
    private val chessvisionRepository = ChessvisionRepository.create()
    private val stockfishRepository = StockfishRepository.create()

    private val startCaptureIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                captureManager.startProjection(result.resultCode, result.data!!)
                startAnalysisLoop()
            }
        }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startOverlayService(this)
            }
        }

    private fun startOverlayServiceWithPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startOverlayService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stopButton = Button(this).apply {
            text = "Stop Overlay"
            setOnClickListener {
                stopOverlayService(this@MainActivity)
                finish()
            }
        }
        setContentView(stopButton)

        captureManager = CaptureManager(this)
        if (Settings.canDrawOverlays(this)) {
            startOverlayServiceWithPermission()
            startCaptureIntent.launch(captureManager.createScreenCaptureIntent())
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            startOverlayServiceWithPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayServiceWithPermission()
                startCaptureIntent.launch(captureManager.createScreenCaptureIntent())
            }
        }
    }

    /**
     * Repeatedly captures the screen, extracts FEN, runs Stockfish analysis and
     * updates the overlay text. Failures in any step only log the error and the
     * loop continues.
     */
    private fun startAnalysisLoop(intervalMillis: Long = 5000L) {
        lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                var bestMove: String? = null

                // Capture screen
                val bitmap = try {
                    withContext(Dispatchers.IO) { captureManager.captureOnce() }
                } catch (e: Exception) {
                    Log.e(TAG, "Capture failed", e)
                    null
                }

                // Extract FEN
                val fen = bitmap?.let {
                    try {
                        chessvisionRepository.extractFenFromImage(it)
                    } catch (e: Exception) {
                        Log.e(TAG, "FEN extraction failed", e)
                        null
                    } finally {
                        it.recycle()
                    }
                }

                // Analyze with Stockfish
                bestMove = fen?.let {
                    try {
                        stockfishRepository.analyzeFen(it)
                    } catch (e: Exception) {
                        Log.e(TAG, "Stockfish analysis failed", e)
                        null
                    }
                }

                // Update overlay on the main thread
                bestMove?.let {
                    withContext(Dispatchers.Main) {
                        updateOverlayText(it)
                    }
                }

                delay(intervalMillis)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.stopProjection()
        stopOverlayService(this)
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1002
        private const val TAG = "MainActivity"
    }
}

