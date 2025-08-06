package com.example

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.capture.CaptureManager
import com.example.api.ChessvisionRepository
import com.example.api.StockfishRepository
import com.example.overlay.startOverlayService
import com.example.overlay.stopOverlayService
import com.example.overlay.updateOverlayText
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Main activity that ties together screen capture, FEN extraction, Stockfish
 * analysis and overlay updates. The flow repeats on a fixed interval.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var captureManager: CaptureManager
    private val chessvisionRepository = ChessvisionRepository.create()
    private val stockfishRepository = StockfishRepository.create()

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
        val started = startOverlayService(this)
        if (!started) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
        startActivityForResult(
            captureManager.createScreenCaptureIntent(),
            REQUEST_CAPTURE
        )
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            startOverlayService(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            captureManager.startProjection(resultCode, data)
            startAnalysisLoop()
        }
    }

    /**
     * Repeatedly captures the screen, extracts FEN, runs Stockfish analysis and
     * updates the overlay text. Failures in any step only log the error and the
     * loop continues.
     */
    private fun startAnalysisLoop(intervalMillis: Long = 5000L) {
        lifecycleScope.launch {
            while (isActive) {
                var bestMove: String? = null

                // Capture screen
                val bitmap = try {
                    captureManager.captureOnce()
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

                // Update overlay
                bestMove?.let { updateOverlayText(it) }

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
        private const val REQUEST_CAPTURE = 1001
        private const val TAG = "MainActivity"
    }
}

