package com.example.data.radio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity

/**
 * Android Foreground Service managing live internet radio audio streaming via MediaPlayer.
 * Ensures uninterrupted background playback, system media notifications, and state synchronization.
 */
class RadioPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val radioManager = RadioManager.getInstance()

    private var currentUrl: String? = null
    private var currentStationName: String = "Folk Radio"

    companion object {
        const val TAG = "RadioPlayerService"
        const val CHANNEL_ID = "radio_playback_channel"
        const val NOTIFICATION_ID = 2001

        const val ACTION_PLAY = "com.example.radio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.radio.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.radio.ACTION_RESUME"
        const val ACTION_TOGGLE = "com.example.radio.ACTION_TOGGLE"
        const val ACTION_STOP = "com.example.radio.ACTION_STOP"
        const val ACTION_SET_VOLUME = "com.example.radio.ACTION_SET_VOLUME"

        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_STATION_NAME = "extra_station_name"
        const val EXTRA_VOLUME = "extra_volume"

        fun play(context: Context, url: String, stationName: String) {
            val intent = Intent(context, RadioPlayerService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_STREAM_URL, url)
                putExtra(EXTRA_STATION_NAME, stationName)
            }
            startServiceSafely(context, intent)
        }

        fun pause(context: Context) {
            val intent = Intent(context, RadioPlayerService::class.java).apply {
                action = ACTION_PAUSE
            }
            startServiceSafely(context, intent)
        }

        fun resume(context: Context) {
            val intent = Intent(context, RadioPlayerService::class.java).apply {
                action = ACTION_RESUME
            }
            startServiceSafely(context, intent)
        }

        fun toggle(context: Context) {
            val intent = Intent(context, RadioPlayerService::class.java).apply {
                action = ACTION_TOGGLE
            }
            startServiceSafely(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, RadioPlayerService::class.java).apply {
                action = ACTION_STOP
            }
            startServiceSafely(context, intent)
        }

        fun setVolume(context: Context, volume: Float) {
            val intent = Intent(context, RadioPlayerService::class.java).apply {
                action = ACTION_SET_VOLUME
                putExtra(EXTRA_VOLUME, volume)
            }
            startServiceSafely(context, intent)
        }

        private fun startServiceSafely(context: Context, intent: Intent) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Cannot start service in background, fallback to direct manager", e)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_STREAM_URL)
                val stationName = intent.getStringExtra(EXTRA_STATION_NAME) ?: "Live Folk Radio"
                if (!url.isNullOrBlank()) {
                    currentStationName = stationName
                    startStream(url)
                }
            }
            ACTION_PAUSE -> pauseStream()
            ACTION_RESUME -> resumeStream()
            ACTION_TOGGLE -> togglePlayPause()
            ACTION_STOP -> stopStream()
            ACTION_SET_VOLUME -> {
                val vol = intent.getFloatExtra(EXTRA_VOLUME, 1.0f)
                applyVolume(vol)
            }
        }

        return START_NOT_STICKY
    }

    private fun startStream(url: String) {
        if (currentUrl == url && mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == false) {
                resumeStream()
            }
            return
        }

        releaseMediaPlayer()
        currentUrl = url
        radioManager.notifyLoading(url)

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                val currentVol = radioManager.volume.value
                setVolume(currentVol, currentVol)

                setOnPreparedListener { player ->
                    try {
                        player.start()
                        radioManager.notifyPlaying(true)
                        updateForegroundNotification(isPlaying = true)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting MediaPlayer onPrepared", e)
                        radioManager.notifyError("Playback error: ${e.message}")
                    }
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    radioManager.notifyError("Stream connection error ($what)")
                    updateForegroundNotification(isPlaying = false)
                    true
                }

                prepareAsync()
            }

            // Immediately start foreground with loading notification
            startForeground(NOTIFICATION_ID, buildNotification(isPlaying = false, isLoading = true))
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing MediaPlayer for url: $url", e)
            radioManager.notifyError("Failed to initialize stream: ${e.message}")
        }
    }

    private fun pauseStream() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    radioManager.notifyPlaying(false)
                    updateForegroundNotification(isPlaying = false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing stream", e)
        }
    }

    private fun resumeStream() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    radioManager.notifyPlaying(true)
                    updateForegroundNotification(isPlaying = true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming stream", e)
        }
    }

    private fun togglePlayPause() {
        val player = mediaPlayer
        if (player != null && player.isPlaying) {
            pauseStream()
        } else {
            resumeStream()
        }
    }

    private fun stopStream() {
        releaseMediaPlayer()
        currentUrl = null
        radioManager.notifyPlaying(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun applyVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting volume in service", e)
        }
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing MediaPlayer", e)
        } finally {
            mediaPlayer = null
        }
    }

    private fun acquireWakeLock() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FolkRadio:StreamWakeLock")?.apply {
                setReferenceCounted(false)
                acquire(10 * 60 * 1000L) // 10 minutes timeout safety
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to acquire WakeLock", e)
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to release WakeLock", e)
        } finally {
            wakeLock = null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Folk Radio Audio Streaming",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls and status for live radio audio streaming"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun updateForegroundNotification(isPlaying: Boolean) {
        try {
            val notification = buildNotification(isPlaying = isPlaying, isLoading = false)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update notification", e)
        }
    }

    private fun buildNotification(isPlaying: Boolean, isLoading: Boolean): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Action: Toggle Play/Pause
        val toggleActionIntent = Intent(this, RadioPlayerService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_RESUME
        }
        val togglePendingIntent = PendingIntent.getService(
            this,
            1,
            toggleActionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Action: Stop
        val stopActionIntent = Intent(this, RadioPlayerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            2,
            stopActionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val statusText = when {
            isLoading -> "Подключение к эфиру..."
            isPlaying -> "🔴 Прямой эфир (Live Stream)"
            else -> "Пауза"
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentStationName)
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(isPlaying || isLoading)
            .setOnlyAlertOnce(true)
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "Пауза" else "Слушать",
                togglePendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Стоп",
                stopPendingIntent
            )

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        releaseWakeLock()
    }
}
