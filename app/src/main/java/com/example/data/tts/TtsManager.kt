package com.example.data.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class TtsNarratorItem(
    val id: String,
    val text: String,
    val title: String,
    val subtitle: String = ""
)

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _currentSpokenId = MutableStateFlow<String?>(null)
    val currentSpokenId: StateFlow<String?> = _currentSpokenId.asStateFlow()

    private val _currentTitle = MutableStateFlow<String?>(null)
    val currentTitle: StateFlow<String?> = _currentTitle.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _voicePreset = MutableStateFlow("classic") // "elder", "classic", "cheerful"
    val voicePreset: StateFlow<String> = _voicePreset.asStateFlow()

    private val _playlist = MutableStateFlow<List<TtsNarratorItem>>(emptyList())
    val playlist: StateFlow<List<TtsNarratorItem>> = _playlist.asStateFlow()

    private val _playlistIndex = MutableStateFlow(-1)
    val playlistIndex: StateFlow<Int> = _playlistIndex.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    var onSpeechStarted: (() -> Unit)? = null
    var onSpeechStopped: (() -> Unit)? = null

    private var nextItemJob: Job? = null
    private var currentLanguageName: String = "Russian"

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TtsManager", "Failed to construct TextToSpeech", e)
            _lastError.value = "TTS init error: ${e.message}"
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    scope.launch {
                        _isSpeaking.value = true
                        _isPaused.value = false
                        _currentSpokenId.value = utteranceId
                    }
                }

                override fun onDone(utteranceId: String?) {
                    scope.launch {
                        handleUtteranceCompleted(utteranceId)
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    scope.launch {
                        _isSpeaking.value = false
                        _currentSpokenId.value = null
                        _lastError.value = "Speech failed on utterance $utteranceId"
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    scope.launch {
                        _isSpeaking.value = false
                        _currentSpokenId.value = null
                        _lastError.value = "Speech error (code $errorCode)"
                    }
                }
            })

            applyPitchAndRate()
            applyLocale(currentLanguageName)
            _isReady.value = true
            Log.d("TtsManager", "TextToSpeech successfully initialized")
        } else {
            Log.e("TtsManager", "TextToSpeech onInit failed with status $status")
            _isReady.value = false
            _lastError.value = "TextToSpeech engine initialization failed (status: $status)"
        }
    }

    private fun handleUtteranceCompleted(utteranceId: String?) {
        val currentList = _playlist.value
        val currentIndex = _playlistIndex.value
        if (currentList.isNotEmpty() && currentIndex in currentList.indices) {
            val nextIndex = currentIndex + 1
            if (nextIndex < currentList.size) {
                nextItemJob?.cancel()
                nextItemJob = scope.launch {
                    delay(380) // Breath pause between jokes for natural storytelling rhythm
                    playPlaylistItem(nextIndex)
                }
            } else {
                // Completed whole session
                _isSpeaking.value = false
                _isPaused.value = false
                _currentSpokenId.value = null
                _currentTitle.value = null
                _playlistIndex.value = -1
                _playlist.value = emptyList()
                onSpeechStopped?.invoke()
            }
        } else {
            _isSpeaking.value = false
            _isPaused.value = false
            _currentSpokenId.value = null
            _currentTitle.value = null
            onSpeechStopped?.invoke()
        }
    }

    fun setLanguage(languageName: String) {
        currentLanguageName = languageName
        if (_isReady.value) {
            applyLocale(languageName)
        }
    }

    private fun applyLocale(languageName: String) {
        val locale = getLocaleForLanguage(languageName)
        tts?.let { engine ->
            val result = engine.isLanguageAvailable(locale)
            if (result == TextToSpeech.LANG_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
            ) {
                engine.language = locale
            } else {
                Log.w("TtsManager", "Language $locale is not fully available, falling back to US")
                engine.language = Locale.US
            }
        }
    }

    fun getLocaleForLanguage(languageName: String): Locale {
        return when (languageName.trim().lowercase()) {
            "russian", "русский" -> Locale.forLanguageTag("ru-RU")
            "english" -> Locale.US
            "spanish", "español" -> Locale.forLanguageTag("es-ES")
            "chinese", "中文" -> Locale.SIMPLIFIED_CHINESE
            "hindi", "हिन्दी" -> Locale.forLanguageTag("hi-IN")
            "arabic", "العربية" -> Locale.forLanguageTag("ar")
            "french", "français" -> Locale.FRENCH
            "portuguese", "português" -> Locale.forLanguageTag("pt-BR")
            "german", "deutsch" -> Locale.GERMAN
            "japanese", "日本語" -> Locale.JAPANESE
            else -> Locale.US
        }
    }

    private fun applyPitchAndRate() {
        tts?.let { engine ->
            engine.setPitch(_pitch.value)
            engine.setSpeechRate(_speechRate.value)
        }
    }

    fun setVoicePreset(preset: String) {
        _voicePreset.value = preset
        when (preset) {
            "elder" -> {
                _pitch.value = 0.82f
                _speechRate.value = 0.90f
            }
            "cheerful" -> {
                _pitch.value = 1.18f
                _speechRate.value = 1.12f
            }
            else -> { // "classic"
                _pitch.value = 1.0f
                _speechRate.value = 1.0f
            }
        }
        applyPitchAndRate()
    }

    fun cycleSpeechRate() {
        val nextRate = when (_speechRate.value) {
            0.85f -> 1.0f
            1.0f -> 1.25f
            1.25f -> 1.5f
            else -> 0.85f
        }
        _speechRate.value = nextRate
        applyPitchAndRate()
    }

    fun setCustomRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.6f, 2.0f)
        applyPitchAndRate()
    }

    fun setCustomPitch(pitch: Float) {
        _pitch.value = pitch.coerceIn(0.6f, 1.8f)
        applyPitchAndRate()
    }

    fun speakSingle(id: String, text: String, title: String, languageName: String) {
        if (_currentSpokenId.value == id && _isSpeaking.value) {
            // Already speaking this exact item, toggle off
            stop()
            return
        }

        stop()
        setLanguage(languageName)
        applyPitchAndRate()

        _playlist.value = emptyList()
        _playlistIndex.value = -1
        _currentSpokenId.value = id
        _currentTitle.value = title
        _isSpeaking.value = true
        _isPaused.value = false

        onSpeechStarted?.invoke()

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, id)
    }

    fun startStorytellerSession(
        intro: String,
        jokes: List<String>,
        outro: String,
        languageName: String,
        startIndex: Int = 0
    ) {
        stop()
        setLanguage(languageName)
        applyPitchAndRate()

        val items = mutableListOf<TtsNarratorItem>()
        if (intro.isNotBlank()) {
            items.add(TtsNarratorItem(id = "storyteller_intro", text = intro, title = "Сказитель: Вступление", subtitle = "Вводное слово"))
        }

        jokes.forEachIndexed { i, joke ->
            items.add(
                TtsNarratorItem(
                    id = "joke_${i + 1}",
                    text = joke,
                    title = "Анекдот #${i + 1}",
                    subtitle = "из ${jokes.size}"
                )
            )
        }

        if (outro.isNotBlank()) {
            items.add(TtsNarratorItem(id = "storyteller_outro", text = outro, title = "Сказитель: Напутствие", subtitle = "Заключительное слово"))
        }

        if (items.isEmpty()) return

        _playlist.value = items
        val validStart = startIndex.coerceIn(0, items.size - 1)
        playPlaylistItem(validStart)
    }

    fun playCustomPlaylist(
        items: List<TtsNarratorItem>,
        languageName: String,
        startIndex: Int = 0
    ) {
        stop()
        setLanguage(languageName)
        applyPitchAndRate()

        if (items.isEmpty()) return

        _playlist.value = items
        val validStart = startIndex.coerceIn(0, items.size - 1)
        playPlaylistItem(validStart)
    }

    private fun playPlaylistItem(index: Int) {
        val items = _playlist.value
        if (index !in items.indices) return

        _playlistIndex.value = index
        val item = items[index]
        _currentSpokenId.value = item.id
        _currentTitle.value = "${item.title} ${item.subtitle}".trim()
        _isSpeaking.value = true
        _isPaused.value = false

        onSpeechStarted?.invoke()

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, item.id)
        }
        tts?.speak(item.text, TextToSpeech.QUEUE_FLUSH, params, item.id)
    }

    fun pause() {
        if (_isSpeaking.value) {
            nextItemJob?.cancel()
            tts?.stop()
            _isSpeaking.value = false
            _isPaused.value = true
        }
    }

    fun resume() {
        if (_isPaused.value) {
            val items = _playlist.value
            val idx = _playlistIndex.value
            if (items.isNotEmpty() && idx in items.indices) {
                playPlaylistItem(idx)
            } else {
                // Resume single text if available
                _isPaused.value = false
            }
        }
    }

    fun next() {
        nextItemJob?.cancel()
        val items = _playlist.value
        val nextIdx = _playlistIndex.value + 1
        if (items.isNotEmpty() && nextIdx < items.size) {
            tts?.stop()
            playPlaylistItem(nextIdx)
        } else {
            stop()
        }
    }

    fun previous() {
        nextItemJob?.cancel()
        val items = _playlist.value
        val prevIdx = _playlistIndex.value - 1
        if (items.isNotEmpty() && prevIdx >= 0) {
            tts?.stop()
            playPlaylistItem(prevIdx)
        } else if (items.isNotEmpty()) {
            // Replay first
            playPlaylistItem(0)
        }
    }

    fun stop() {
        nextItemJob?.cancel()
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "Error stopping TTS", e)
        }
        _isSpeaking.value = false
        _isPaused.value = false
        _currentSpokenId.value = null
        _currentTitle.value = null
        _playlistIndex.value = -1
        _playlist.value = emptyList()
        onSpeechStopped?.invoke()
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.e("TtsManager", "Error shutting down TTS", e)
        }
    }
}
