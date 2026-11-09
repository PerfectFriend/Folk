package com.example.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural campfire sound generator for folk storytelling ambience.
 * Generates natural warm fire rumble (brown noise) and spontaneous wood ember crackles
 * directly in memory using Android's native AudioTrack, without needing large audio assets.
 */
class CampfireAmbiencePlayer {

    private var audioTrack: AudioTrack? = null
    private var generationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(0.45f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    fun toggle() {
        if (_isPlaying.value) {
            stop()
        } else {
            start()
        }
    }

    fun start() {
        if (_isPlaying.value) return

        try {
            val sampleRate = 22050
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(sampleRate / 4)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            _isPlaying.value = true

            generationJob = scope.launch {
                val chunkSize = 1024
                val buffer = ShortArray(chunkSize)

                var lastBrown = 0.0
                var crackleDecay = 0.0
                var crackleFreq = 800.0
                var cracklePhase = 0.0

                while (isActive && _isPlaying.value) {
                    val currentVol = _volume.value
                    val random = Random.Default

                    for (i in 0 until chunkSize) {
                        // 1. Warm Brown Noise (low-pass filtered white noise for fire body)
                        val white = (random.nextDouble() * 2.0 - 1.0)
                        lastBrown = (lastBrown * 0.94) + (white * 0.06)

                        // 2. Random Wood Ember Crackles & Pops
                        // Probability of a new spark/snap per sample
                        if (random.nextDouble() < 0.0018) {
                            crackleDecay = 1.0
                            crackleFreq = 600.0 + random.nextDouble() * 1400.0
                            cracklePhase = 0.0
                        }

                        var crackleSample = 0.0
                        if (crackleDecay > 0.001) {
                            cracklePhase += 2.0 * Math.PI * crackleFreq / sampleRate
                            crackleSample = sin(cracklePhase) * crackleDecay
                            crackleDecay *= 0.985 // Exponential decay of pop
                        }

                        // Combined mixed signal
                        val mixed = (lastBrown * 0.55 + crackleSample * 0.85) * currentVol
                        val clamped = mixed.coerceIn(-1.0, 1.0)
                        buffer[i] = (clamped * 32767.0).toInt().toShort()
                    }

                    audioTrack?.write(buffer, 0, chunkSize)
                }
            }
        } catch (e: Exception) {
            Log.e("CampfirePlayer", "Failed to start campfire ambience", e)
            stop()
        }
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0.0f, 1.0f)
    }

    fun stop() {
        _isPlaying.value = false
        generationJob?.cancel()
        generationJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e("CampfirePlayer", "Error stopping AudioTrack", e)
        }
        audioTrack = null
    }
}
