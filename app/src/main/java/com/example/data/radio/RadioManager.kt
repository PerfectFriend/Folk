package com.example.data.radio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RadioManager private constructor() {

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentStreamUrl = MutableStateFlow<String?>(null)
    val currentStreamUrl: StateFlow<String?> = _currentStreamUrl.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _isLiveEdge = MutableStateFlow(true)
    val isLiveEdge: StateFlow<Boolean> = _isLiveEdge.asStateFlow()

    private val coroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main + kotlinx.coroutines.SupervisorJob())
    private var progressJob: kotlinx.coroutines.Job? = null
    private var sessionStartTime = 0L

    companion object {
        @Volatile
        private var instance: RadioManager? = null

        fun getInstance(): RadioManager {
            return instance ?: synchronized(this) {
                instance ?: RadioManager().also { instance = it }
            }
        }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        sessionStartTime = System.currentTimeMillis() - _currentPositionMs.value
        progressJob = coroutineScope.launch {
            while (isActive) {
                try {
                    val player = mediaPlayer
                    if (player != null && _isPlaying.value) {
                        val pos = try { player.currentPosition.toLong() } catch (e: Exception) { 0L }
                        val dur = try { player.duration.toLong() } catch (e: Exception) { -1L }

                        val calculatedPos = if (pos > 0) pos else (System.currentTimeMillis() - sessionStartTime).coerceAtLeast(0L)
                        _currentPositionMs.value = calculatedPos

                        if (dur > 0) {
                            _durationMs.value = dur
                        } else {
                            // Live stream: the duration represents the active live buffer window
                            _durationMs.value = calculatedPos.coerceAtLeast(60_000L)
                        }
                    }
                } catch (e: Exception) {
                    // Ignore state errors during fast transitions
                }
                kotlinx.coroutines.delay(500)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playStream(url: String) {
        if (_currentStreamUrl.value == url && mediaPlayer != null) {
            if (!mediaPlayer!!.isPlaying) {
                try {
                    mediaPlayer!!.start()
                    _isPlaying.value = true
                    startProgressTracking()
                } catch (e: Exception) {
                    Log.e("RadioManager", "Error resuming stream", e)
                    _error.value = "Playback error: ${e.message}"
                }
            }
            return
        }

        stopStream()

        _currentStreamUrl.value = url
        _isLoading.value = true
        _error.value = null
        _currentPositionMs.value = 0L
        _isLiveEdge.value = true

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setVolume(_volume.value, _volume.value)
                setOnPreparedListener {
                    _isLoading.value = false
                    try {
                        start()
                        _isPlaying.value = true
                        startProgressTracking()
                    } catch (e: Exception) {
                        Log.e("RadioManager", "Error starting player in onPrepared", e)
                        _error.value = "Failed to start stream: ${e.message}"
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("RadioManager", "MediaPlayer error: what=$what, extra=$extra")
                    _isLoading.value = false
                    _isPlaying.value = false
                    stopProgressTracking()
                    _error.value = "Stream connection failed (code: $what)"
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("RadioManager", "Error preparing stream", e)
            _isLoading.value = false
            _isPlaying.value = false
            stopProgressTracking()
            _error.value = "Failed to initialize stream: ${e.message}"
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            pauseStream()
        } else {
            resumeStream()
        }
    }

    fun pauseStream() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                stopProgressTracking()
            }
        }
    }

    fun resumeStream() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                startProgressTracking()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        val targetMs = positionMs.coerceAtLeast(0L)
        _currentPositionMs.value = targetMs
        val dur = _durationMs.value
        _isLiveEdge.value = (dur <= 0 || targetMs >= dur - 3000L)

        mediaPlayer?.let { player ->
            try {
                player.seekTo(targetMs.toInt())
            } catch (e: Exception) {
                Log.e("RadioManager", "Error seeking to $targetMs", e)
            }
        }
    }

    fun seekToLive() {
        val dur = _durationMs.value
        if (dur > 0) {
            seekTo(dur)
        }
        _isLiveEdge.value = true
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0.0f, 1.0f)
        _volume.value = clamped
        mediaPlayer?.let { player ->
            try {
                player.setVolume(clamped, clamped)
            } catch (e: Exception) {
                Log.e("RadioManager", "Error adjusting volume", e)
            }
        }
    }

    fun stopStream() {
        stopProgressTracking()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("RadioManager", "Error stopping player", e)
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _isLoading.value = false
            _currentPositionMs.value = 0L
            _isLiveEdge.value = true
        }
    }
}
