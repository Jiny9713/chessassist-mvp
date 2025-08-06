package com.example.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat

/**
 * Foreground service that displays the latest best move text in a small
 * overlay on top of any application.
 */
class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: TextView? = null

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        addOverlay()
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        removeOverlay()
        instance = null
        super.onDestroy()
    }

    private fun addOverlay() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
        }

        overlayView = TextView(this).apply {
            text = "Best move: --"
            setBackgroundColor(0xF0FFFFFF.toInt())
            setTextColor(0xFF000000.toInt())
            textSize = 16f
            val pad = dp(8)
            setPadding(pad, pad, pad, pad)
        }

        windowManager?.addView(overlayView, params)
    }

    private fun removeOverlay() {
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
    }

    private fun createNotification(): Notification {
        val channelId = "overlay_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Overlay Service")
            .setContentText("Showing best move")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    /** Update overlay text with the latest best move. */
    private fun updateText(text: String) {
        overlayView?.post {
            overlayView?.text = "Best move: $text"
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private var instance: OverlayService? = null

        /**
         * Starts the overlay service if permission has been granted.
         *
         * @return `true` if the service was started, `false` if permission is
         * missing and the caller should request it.
         */
        fun start(context: Context): Boolean {
            if (!Settings.canDrawOverlays(context)) {
                return false
            }

            val intent = Intent(context, OverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            return true
        }

        /** Stops the overlay service and removes the overlay view. */
        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            context.stopService(intent)
        }

        /** Updates the overlay with a new best move string. */
        fun updateOverlayText(text: String) {
            instance?.updateText(text)
        }
    }
}

/** Convenience function to start [OverlayService]. */
fun startOverlayService(context: Context): Boolean = OverlayService.start(context)

/** Convenience function to stop [OverlayService]. */
fun stopOverlayService(context: Context) = OverlayService.stop(context)

/** Convenience function to update the overlay text. */
fun updateOverlayText(text: String) = OverlayService.updateOverlayText(text)

