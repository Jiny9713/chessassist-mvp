package com.example.capture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Handles screen capture using [MediaProjection]. Each capture returns a [Bitmap]
 * to the caller.
 *
 * Manifest requirements:
 * ```xml
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
 * ```
 */
class CaptureManager(private val context: Context) {

    companion object {
        private const val TAG = "CaptureManager"
        private const val MAX_IMAGES = 2
        // Default capture interval suitable for testing; tweak per device performance.
        private const val DEFAULT_FRAME_INTERVAL_MS = 1000L
    }

    private val projectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: android.hardware.display.VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var captureThread: HandlerThread? = null
    private var captureHandler: Handler? = null
    private val bitmapQueue = LinkedBlockingQueue<Bitmap>(1)
    private var periodicCaptureJob: Job? = null

    /** Create an intent to request screen capture permission. */
    fun createScreenCaptureIntent(): Intent = projectionManager.createScreenCaptureIntent()

    /** Start projection after user consent. */
    fun startProjection(resultCode: Int, data: Intent) {
        mediaProjection = projectionManager.getMediaProjection(resultCode, data)
        setupCaptureResources()
    }

    private fun setupCaptureResources() {
        val metrics = context.resources.displayMetrics
        val density = metrics.densityDpi
        // Use half of the screen resolution to reduce memory/CPU usage; adjust as needed.
        val width = metrics.widthPixels / 2
        val height = metrics.heightPixels / 2

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, MAX_IMAGES)
        captureThread = HandlerThread("ScreenCapture").apply { start() }
        captureHandler = Handler(captureThread!!.looper)
        imageReader!!.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val plane = image.planes[0]
            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val w = image.width
            val h = image.height
            val rowPadding = rowStride - pixelStride * w

            val bitmap = Bitmap.createBitmap(
                w + rowPadding / pixelStride,
                h,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            val cropped = Bitmap.createBitmap(bitmap, 0, 0, w, h)
            bitmap.recycle()
            image.close()
            if (!bitmapQueue.offer(cropped)) {
                cropped.recycle()
            }
            Log.d(TAG, "Captured bitmap ${cropped.width}x${cropped.height}")
        }, captureHandler)

        val surface: Surface = imageReader!!.surface
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "capture",
            width,
            height,
            density,
            0,
            surface,
            null,
            captureHandler
        )
        Log.d(TAG, "Virtual display created: ${width}x${height}")
    }

    /**
     * Capture screen on a fixed interval using coroutines.
     * The default interval of [DEFAULT_FRAME_INTERVAL_MS] works well for testing
     * but can be tuned per device for performance.
     */
    fun startPeriodicCapture(frameIntervalMs: Long = DEFAULT_FRAME_INTERVAL_MS) {
        periodicCaptureJob?.cancel()
        periodicCaptureJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                captureOnce()
                delay(frameIntervalMs)
            }
        }
    }

    /** Stop the periodic capture coroutine if running. */
    fun stopPeriodicCapture() {
        periodicCaptureJob?.cancel()
        periodicCaptureJob = null
    }

    /**
     * Capture a single frame and return it as a [Bitmap].
     * Waits up to [timeoutMs] milliseconds for an image and returns `null`
     * if none is available.
     */
    fun captureOnce(timeoutMs: Long = 0): Bitmap? =
        if (timeoutMs > 0) {
            bitmapQueue.poll(timeoutMs, TimeUnit.MILLISECONDS)
        } else {
            bitmapQueue.poll()
        }

    /** Stop capturing and release resources. */
    fun stopProjection() {
        stopPeriodicCapture()
        captureHandler?.removeCallbacksAndMessages(null)
        captureThread?.quitSafely()
        captureThread = null

        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        var b = bitmapQueue.poll()
        while (b != null) {
            b.recycle()
            b = bitmapQueue.poll()
        }

        mediaProjection?.stop()
        mediaProjection = null
        Log.d(TAG, "Projection stopped")
    }

}

/** Example activity showing how to request permission and start captures. */
class ScreenCaptureActivity : AppCompatActivity() {

    private lateinit var captureManager: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureManager = CaptureManager(this)
        startActivityForResult(captureManager.createScreenCaptureIntent(), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            captureManager.startProjection(resultCode, data)
            captureManager.startPeriodicCapture(5000L)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.stopProjection()
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
