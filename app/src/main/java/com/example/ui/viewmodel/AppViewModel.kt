package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.FallbackData
import com.example.data.ai.AkynGeminiEngine
import com.example.data.auth.AuthManager
import com.example.data.firestore.AkynFirestoreRepository
import com.google.firebase.auth.FirebaseUser
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GeminiApiClient
import com.example.data.api.Part
import com.example.data.api.GenerationConfig
import com.example.data.database.AppDatabase
import com.example.data.database.AppRepository
import com.example.data.database.NewsDigest
import com.example.data.database.RadioStation
import com.example.data.database.SavedJoke
import com.example.data.audio.CampfireAmbiencePlayer
import com.example.data.radio.RadioManager
import com.example.data.radio.RadioPlayerService
import com.example.data.tts.TtsManager
import com.example.data.tts.TtsNarratorItem
import com.example.data.api.Tool
import com.example.data.chat.ChatMessage
import com.example.data.chat.GeminiChatModel
import com.example.data.chat.LiveVoiceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("folk_anecdotes_prefs", Context.MODE_PRIVATE)

    // Room Database
    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "folk_anecdotes_database"
    ).fallbackToDestructiveMigration().build()

    private val repository = AppRepository(database.appDao())

    // Google Authentication & Firestore Cloud Sync
    val authManager = AuthManager.getInstance()
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    val akynEngine = AkynGeminiEngine()
    val firestoreRepository = AkynFirestoreRepository.create(application)

    // Language selection
    private val _selectedLanguage = MutableStateFlow(sharedPrefs.getString("language", "English") ?: "English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Background style selection
    private val _backgroundStyle = MutableStateFlow(sharedPrefs.getString("background_style", "Registan") ?: "Registan")
    val backgroundStyle: StateFlow<String> = _backgroundStyle.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow(sharedPrefs.getBoolean("is_first_launch", true))
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    // Database Observables
    val savedJokes: StateFlow<List<SavedJoke>> = repository.savedJokes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val radioStations: StateFlow<List<RadioStation>> = repository.radioStations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsDigests: StateFlow<List<NewsDigest>> = repository.newsDigests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Storyteller Generator states
    private val _storytellerLoading = MutableStateFlow(false)
    val storytellerLoading: StateFlow<Boolean> = _storytellerLoading.asStateFlow()

    private val _storytellerIntro = MutableStateFlow("")
    val storytellerIntro: StateFlow<String> = _storytellerIntro.asStateFlow()

    private val _storytellerJokes = MutableStateFlow<List<String>>(emptyList())
    val storytellerJokes: StateFlow<List<String>> = _storytellerJokes.asStateFlow()

    private val _storytellerOutro = MutableStateFlow("")
    val storytellerOutro: StateFlow<String> = _storytellerOutro.asStateFlow()

    private val _storytellerCulture = MutableStateFlow("Jewish")
    val storytellerCulture: StateFlow<String> = _storytellerCulture.asStateFlow()

    private val _storytellerSituation = MutableStateFlow("")
    val storytellerSituation: StateFlow<String> = _storytellerSituation.asStateFlow()

    private val _storytellerError = MutableStateFlow<String?>(null)
    val storytellerError: StateFlow<String?> = _storytellerError.asStateFlow()

    // Spotlight/Daily anecdote state
    private val _spotlightJokeIndex = MutableStateFlow(0)
    val spotlightJokeIndex: StateFlow<Int> = _spotlightJokeIndex.asStateFlow()

    // Selected Trend Topic
    private val _selectedTrendTopic = MutableStateFlow<String?>(null)
    val selectedTrendTopic: StateFlow<String?> = _selectedTrendTopic.asStateFlow()

    // Selected TOP Culture Filter
    private val _selectedTopCulture = MutableStateFlow("all")
    val selectedTopCulture: StateFlow<String> = _selectedTopCulture.asStateFlow()

    // --- MULTI-TURN GEMINI CHATBOT STATE ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading.asStateFlow()

    private val _chatModel = MutableStateFlow(GeminiChatModel.FLASH)
    val chatModel: StateFlow<GeminiChatModel> = _chatModel.asStateFlow()

    private val _chatUseSearchGrounding = MutableStateFlow(false)
    val chatUseSearchGrounding: StateFlow<Boolean> = _chatUseSearchGrounding.asStateFlow()

    // --- LIVE VOICE CONVERSATIONS (gemini-3.8-live) STATE ---
    private val _liveVoiceState = MutableStateFlow(LiveVoiceState())
    val liveVoiceState: StateFlow<LiveVoiceState> = _liveVoiceState.asStateFlow()

    // News Digest state
    private val _newsLoading = MutableStateFlow(false)
    val newsLoading: StateFlow<Boolean> = _newsLoading.asStateFlow()

    private val _newsError = MutableStateFlow<String?>(null)
    val newsError: StateFlow<String?> = _newsError.asStateFlow()

    // Trends Accounts state - AlphaSignalAI primary source
    private val _monitoredAccounts = MutableStateFlow<List<String>>(
        listOf("@AlphaSignalAI", "@TechCrunch", "@Reuters", "@NASA", "@folklore")
    )
    val monitoredAccounts: StateFlow<List<String>> = _monitoredAccounts.asStateFlow()

    private val _selectedAccounts = MutableStateFlow<Set<String>>(
        setOf("@AlphaSignalAI", "@TechCrunch", "@Reuters", "@NASA", "@folklore")
    )
    val selectedAccounts: StateFlow<Set<String>> = _selectedAccounts.asStateFlow()

    // Radio states
    val radioManager = RadioManager.getInstance()
    
    // Radio player engine choice
    private val _useHtml5Player = MutableStateFlow(false)
    val useHtml5Player: StateFlow<Boolean> = _useHtml5Player.asStateFlow()

    // Combined/Forwarded states
    private val _isRadioPlaying = MutableStateFlow(false)
    val isRadioPlaying: StateFlow<Boolean> = _isRadioPlaying.asStateFlow()

    private val _isRadioLoading = MutableStateFlow(false)
    val isRadioLoading: StateFlow<Boolean> = _isRadioLoading.asStateFlow()

    private val _currentRadioUrl = MutableStateFlow<String?>(null)
    val currentRadioUrl: StateFlow<String?> = _currentRadioUrl.asStateFlow()

    private val _radioError = MutableStateFlow<String?>(null)
    val radioError: StateFlow<String?> = _radioError.asStateFlow()

    // Selected active station (for UI purposes)
    private val _selectedStation = MutableStateFlow<RadioStation?>(null)
    val selectedStation: StateFlow<RadioStation?> = _selectedStation.asStateFlow()

    val radioPositionMs: StateFlow<Long> = radioManager.currentPositionMs
    val radioDurationMs: StateFlow<Long> = radioManager.durationMs
    val radioVolume: StateFlow<Float> = radioManager.volume
    val isLiveEdge: StateFlow<Boolean> = radioManager.isLiveEdge

    // --- TTS (Text-To-Speech) Integration ---
    val ttsManager = TtsManager(application)
    private var wasRadioPlayingBeforeTts = false

    val ttsSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val ttsPaused: StateFlow<Boolean> = ttsManager.isPaused
    val ttsCurrentSpokenId: StateFlow<String?> = ttsManager.currentSpokenId
    val ttsCurrentTitle: StateFlow<String?> = ttsManager.currentTitle
    val ttsSpeechRate: StateFlow<Float> = ttsManager.speechRate
    val ttsPitch: StateFlow<Float> = ttsManager.pitch
    val ttsVoicePreset: StateFlow<String> = ttsManager.voicePreset
    val ttsPlaylistIndex: StateFlow<Int> = ttsManager.playlistIndex
    val ttsPlaylist: StateFlow<List<TtsNarratorItem>> = ttsManager.playlist
    val ttsIsReady: StateFlow<Boolean> = ttsManager.isReady
    val ttsError: StateFlow<String?> = ttsManager.lastError

    // --- Campfire Ambience (Procedural Audio) ---
    val campfirePlayer = CampfireAmbiencePlayer()
    val isCampfirePlaying: StateFlow<Boolean> = campfirePlayer.isPlaying
    val campfireVolume: StateFlow<Float> = campfirePlayer.volume

    // --- Sleep Timer ---
    val sleepTimerSeconds: StateFlow<Int> = radioManager.sleepTimerRemainingSeconds

    init {
        // Configure TTS language and radio coexistence
        ttsManager.setLanguage(_selectedLanguage.value)
        ttsManager.onSpeechStarted = {
            if (_isRadioPlaying.value) {
                wasRadioPlayingBeforeTts = true
                radioManager.pauseStream()
            }
        }
        ttsManager.onSpeechStopped = {
            if (wasRadioPlayingBeforeTts) {
                wasRadioPlayingBeforeTts = false
                radioManager.resumeStream()
            }
        }

        // When sleep timer expires, stop radio, TTS, and campfire ambience
        radioManager.onSleepTimerExpired = {
            ttsManager.stop()
            campfirePlayer.stop()
        }

        // Collect native RadioManager states and forward them if useHtml5Player is false
        viewModelScope.launch {
            radioManager.isPlaying.collect { nativePlaying ->
                if (!_useHtml5Player.value) {
                    _isRadioPlaying.value = nativePlaying
                }
            }
        }
        viewModelScope.launch {
            radioManager.isLoading.collect { nativeLoading ->
                if (!_useHtml5Player.value) {
                    _isRadioLoading.value = nativeLoading
                }
            }
        }
        viewModelScope.launch {
            radioManager.currentStreamUrl.collect { nativeUrl ->
                if (!_useHtml5Player.value) {
                    _currentRadioUrl.value = nativeUrl
                }
            }
        }
        viewModelScope.launch {
            radioManager.error.collect { nativeError ->
                if (!_useHtml5Player.value) {
                    _radioError.value = nativeError
                }
            }
        }

        // Pre-populate database with radio stations and fallback digests if empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.radioStations.collect { stations ->
                if (stations.isEmpty()) {
                    repository.insertRadioStations(FallbackData.defaultStations)
                } else if (_selectedStation.value == null) {
                    _selectedStation.value = stations.firstOrNull { it.isFavorite } ?: stations.firstOrNull()
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.newsDigests.collect { digests ->
                if (digests.isEmpty()) {
                    val isRussian = _selectedLanguage.value == "Russian" || _selectedLanguage.value == "Русский"
                    val baseList = if (isRussian) FallbackData.defaultNewsDigests else FallbackData.getEnglishNewsDigests()
                    val mapped = baseList.mapIndexed { index, d ->
                        val handle = when (index % 5) {
                            0 -> "@AlphaSignalAI"
                            1 -> "@TechCrunch"
                            2 -> "@Reuters"
                            3 -> "@NASA"
                            else -> "@folklore"
                        }
                        val name = when (index % 5) {
                            0 -> "AlphaSignalAI"
                            1 -> "TechCrunch"
                            2 -> "Reuters"
                            3 -> "NASA"
                            else -> "Folklore Observer"
                        }
                        d.copy(authorName = name, authorHandle = handle)
                    }
                    repository.updateNewsDigests(mapped)
                }
            }
        }

        // Attempt silent auto-sign in if authorized Google account exists
        authManager.attemptAutoSignIn(application, viewModelScope)

        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    syncUserProfile()
                }
            }
        }
    }

    fun syncUserProfile() {
        val user = currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.saveUserProfile(
                userId = user.uid,
                culture = _storytellerCulture.value,
                language = _selectedLanguage.value,
                displayName = user.displayName,
                email = user.email
            )
        }
    }

    fun signInWithGoogle(
        activity: Activity,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        onCancelled: () -> Unit = {}
    ) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = {
                syncUserProfile()
                onSuccess()
            },
            onError = onError,
            onCancelled = onCancelled
        )
    }

    fun signOut() {
        authManager.signOut()
    }

    fun selectLanguage(lang: String) {
        _selectedLanguage.value = lang
        sharedPrefs.edit().putString("language", lang).apply()
        ttsManager.setLanguage(lang)
        syncUserProfile()
    }

    fun selectBackgroundStyle(style: String) {
        _backgroundStyle.value = style
        sharedPrefs.edit().putString("background_style", style).apply()
    }

    fun completeFirstLaunch() {
        _isFirstLaunch.value = false
        sharedPrefs.edit().putBoolean("is_first_launch", false).apply()
    }

    fun setStorytellerCulture(culture: String) {
        _storytellerCulture.value = culture
        syncUserProfile()
    }

    fun setStorytellerSituation(situation: String) {
        _storytellerSituation.value = situation
    }

    // Radio controls
    fun playRadio(station: RadioStation) {
        _selectedStation.value = station
        if (_useHtml5Player.value) {
            _currentRadioUrl.value = station.streamUrl
            _isRadioLoading.value = true
            _isRadioPlaying.value = false
            _radioError.value = null
        } else {
            radioManager.playStreamWithService(getApplication<Application>(), station.streamUrl, station.name)
        }
    }

    fun toggleRadio() {
        if (_selectedStation.value == null) {
            _selectedStation.value = radioStations.value.firstOrNull()
        }
        val targetStation = _selectedStation.value ?: return

        if (_currentRadioUrl.value == null || _currentRadioUrl.value != targetStation.streamUrl) {
            playRadio(targetStation)
            return
        }

        if (_useHtml5Player.value) {
            if (_isRadioPlaying.value) {
                _isRadioPlaying.value = false
            } else {
                _isRadioLoading.value = true
                _isRadioPlaying.value = true
            }
        } else {
            radioManager.togglePlayPauseWithService(getApplication<Application>())
        }
    }

    fun stopRadio() {
        if (_useHtml5Player.value) {
            _isRadioPlaying.value = false
            _isRadioLoading.value = false
            _currentRadioUrl.value = null
        } else {
            radioManager.stopStreamWithService(getApplication<Application>())
        }
        // Retain _selectedStation so persistent player still displays the active station
    }

    fun seekRadio(positionMs: Long) {
        radioManager.seekTo(positionMs)
    }

    fun seekRadioToLive() {
        radioManager.seekToLive()
    }

    fun setRadioVolume(vol: Float) {
        try {
            radioManager.setVolumeWithService(getApplication<Application>(), vol)
        } catch (e: Exception) {
            radioManager.setVolume(vol)
        }
    }

    fun toggleRadioMute() {
        try {
            radioManager.toggleMuteWithService(getApplication<Application>())
        } catch (e: Exception) {
            radioManager.toggleMute()
        }
    }

    fun playNextStation() {
        val currentStations = radioStations.value
        if (currentStations.isEmpty()) return
        val currentIndex = currentStations.indexOfFirst { it.streamUrl == _selectedStation.value?.streamUrl }
        val nextIndex = if (currentIndex >= 0) (currentIndex + 1) % currentStations.size else 0
        val nextStation = currentStations[nextIndex]
        playRadio(nextStation)
    }

    fun playPreviousStation() {
        val currentStations = radioStations.value
        if (currentStations.isEmpty()) return
        val currentIndex = currentStations.indexOfFirst { it.streamUrl == _selectedStation.value?.streamUrl }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else currentStations.size - 1
        val prevStation = currentStations[prevIndex]
        playRadio(prevStation)
    }

    fun setUseHtml5Player(useHtml5: Boolean) {
        if (_useHtml5Player.value == useHtml5) return
        
        // Stop currently playing audio on the previous engine
        if (_useHtml5Player.value) {
            _isRadioPlaying.value = false
            _isRadioLoading.value = false
            _currentRadioUrl.value = null
        } else {
            radioManager.stopStream()
        }
        
        _useHtml5Player.value = useHtml5
        _isRadioPlaying.value = false
        _isRadioLoading.value = false
        _currentRadioUrl.value = null
        _radioError.value = null
    }

    fun setHtml5PlayerState(isPlaying: Boolean, isLoading: Boolean, error: String?, streamUrl: String? = null) {
        if (_useHtml5Player.value) {
            _isRadioPlaying.value = isPlaying
            _isRadioLoading.value = isLoading
            _radioError.value = error
            if (streamUrl != null) {
                _currentRadioUrl.value = streamUrl
            }
        }
    }

    fun addCustomRadioStation(name: String, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRadioStation(RadioStation(name = name, streamUrl = url))
        }
    }

    fun toggleRadioFavorite(station: RadioStation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRadioFavorite(station.id, !station.isFavorite)
            if (_selectedStation.value?.id == station.id) {
                _selectedStation.value = _selectedStation.value?.copy(isFavorite = !station.isFavorite)
            }
        }
    }

    fun deleteRadioStation(station: RadioStation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRadioStation(station.id)
        }
    }

    // Saved Jokes database operations
    fun toggleSaveJoke(jokeText: String, culture: String, situation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.isJokeSaved(jokeText)) {
                repository.deleteJokeByText(jokeText)
            } else {
                repository.insertJoke(SavedJoke(jokeText = jokeText, culture = culture, situation = situation))
                currentUser.value?.uid?.let { uid ->
                    firestoreRepository.saveJoke(uid, jokeText, culture, "Весёлый Акын")
                }
            }
        }
    }

    fun isJokeSavedState(jokeText: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val saved = repository.isJokeSaved(jokeText)
            withContext(Dispatchers.Main) {
                onResult(saved)
            }
        }
    }

    fun deleteSavedJoke(joke: SavedJoke) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteJokeById(joke.id)
        }
    }

    // --- GEMINI ACTIONS ---

    private fun cleanJson(raw: String): String {
        return raw.trim()
            .replace("^```json\\s*".toRegex(), "")
            .replace("^```\\s*".toRegex(), "")
            .replace("```$".toRegex(), "")
            .trim()
    }

    fun generate20Jokes() {
        val situation = _storytellerSituation.value.ifBlank { "Any funny situation" }
        val culture = _storytellerCulture.value
        val language = _selectedLanguage.value

        _storytellerLoading.value = true
        _storytellerError.value = null
        _storytellerIntro.value = ""
        _storytellerJokes.value = emptyList()
        _storytellerOutro.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser.value
            val userDisplayName = user?.displayName

            val prompt = """
                Ты — весёлый акын, знающий море смешных баек на любой жизненный случай!
                Жизненная ситуация: "$situation".
                Расскажи ровно 20 коротких, уморительных и жизненных историй, анекдотов или народных баек в традициях фольклора "$culture" (с национальным колоритом, юмором Ходжи Насреддина, одесскими подколками, северными наблюдениями или житейскими тостами).
                Язык: "$language".
                Верни JSON формат:
                {
                  "storytellerIntro": "Весёлое вступление акына под звон домбры...",
                  "jokes": ["История 1...", "История 2...", ...],
                  "storytellerOutro": "Заключительное остроумное слово акына..."
                }
                Верни только сырой JSON без markdown ```json.
            """.trimIndent()

            val result = akynEngine.generateStory(
                prompt = prompt,
                culture = culture,
                language = language,
                userDisplayName = userDisplayName
            )

            result.onSuccess { responseText ->
                try {
                    val cleanText = cleanJson(responseText)
                    val json = JSONObject(cleanText)
                    val intro = json.optString("storytellerIntro", "Слушайте, добрые люди!")
                    val outro = json.optString("storytellerOutro", "Вот такая песня акына!")
                    val jokesArray = json.getJSONArray("jokes")
                    val list = mutableListOf<String>()
                    for (i in 0 until jokesArray.length()) {
                        list.add(jokesArray.getString(i))
                    }
                    withContext(Dispatchers.Main) {
                        _storytellerIntro.value = intro
                        _storytellerJokes.value = list
                        _storytellerOutro.value = outro
                        _storytellerLoading.value = false
                    }
                } catch (e: Exception) {
                    loadOfflineJokes(culture, "Не удалось разобрать ответ акына.")
                }
            }.onFailure { err ->
                Log.e("AppViewModel", "Akyn story generation failed", err)
                loadOfflineJokes(culture, "Акын оффлайн: ${err.message}")
            }
        }
    }

    private suspend fun loadOfflineJokes(culture: String, errorMsg: String? = null) {
        val isRussian = _selectedLanguage.value == "Russian" || _selectedLanguage.value == "Русский"
        val offline = FallbackData.getFallbackJokes(culture, isRussian)
        withContext(Dispatchers.Main) {
            _storytellerIntro.value = if (isRussian) {
                "Ах, дорогой мой человек, присаживайся ближе к нашему костру! К сожалению, связь с духами космоса (интернетом) прервалась, но у меня всегда припасена старая добрая мудрость народа..."
            } else {
                "Ah, sit closer to the campfire, my friend! Unfortunately, our connection to the dynamic AI spirits is offline, but I always have some classic folk wisdom saved..."
            }
            _storytellerJokes.value = offline
            _storytellerOutro.value = if (isRussian) "Заходи почаще, ещё не то услышишь!" else "Come back soon, I have plenty more!"
            _storytellerError.value = errorMsg // Show notice in UI
            _storytellerLoading.value = false
        }
    }

    fun toggleAccountSelection(account: String) {
        val current = _selectedAccounts.value.toMutableSet()
        if (current.contains(account)) {
            if (current.size > 1) {
                current.remove(account)
            }
        } else {
            current.add(account)
        }
        _selectedAccounts.value = current
    }

    fun addCustomAccount(account: String) {
        val clean = if (account.startsWith("@")) account.trim() else "@${account.trim()}"
        if (clean.length > 1 && !_monitoredAccounts.value.contains(clean)) {
            val updatedList = _monitoredAccounts.value + clean
            _monitoredAccounts.value = updatedList
            val updatedSelected = _selectedAccounts.value + clean
            _selectedAccounts.value = updatedSelected
        }
    }

    fun removeCustomAccount(account: String) {
        val updatedList = _monitoredAccounts.value.filter { it != account }
        if (updatedList.isNotEmpty()) {
            _monitoredAccounts.value = updatedList
            val updatedSelected = _selectedAccounts.value.filter { it != account }.toSet()
            _selectedAccounts.value = if (updatedSelected.isEmpty()) setOf(updatedList.first()) else updatedSelected
        }
    }

    fun generateXDigest(topic: String? = null) {
        val language = _selectedLanguage.value
        _newsLoading.value = true
        _newsError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback to local digests
                loadOfflineDigests(topic = topic)
                return@launch
            }

            val selectedList = _selectedAccounts.value.toList()
            val accountsString = selectedList.joinToString(", ")
            val topicFocusPrompt = if (topic.isNullOrBlank()) {
                "representing recent posts/updates from these specific accounts: $accountsString"
            } else {
                "representing recent posts/updates from these specific accounts: $accountsString, filtered/focused on the search/topic: \"$topic\""
            }

            val prompt = """
                You are an AI trend watcher tracking global breakthroughs and insights from AlphaSignalAI and curated news sources: $accountsString.
                Generate exactly 5 news digest items/posts $topicFocusPrompt.
                For each item, you MUST provide:
                1. "authorName": The display name of the account (e.g. "AlphaSignalAI" for @AlphaSignalAI, "NASA" for @NASA, "TechCrunch" for @TechCrunch, etc. If it is a custom account handle, make up a fitting real name).
                2. "authorHandle": The exact handle including the @ symbol (e.g., "@AlphaSignalAI", "@NASA"). It MUST be one of the accounts requested in the list: $accountsString.
                3. "title": A catchy headline for the post (written in "$language").
                4. "content": A 2-3 sentence trend update text representing the account's recent activity (written in "$language").
                5. "attachedJoke": A highly fitting folklore joke (Uzbek, Armenian, Jewish, Chukchi, Yakut, Kamchatka, American, or African style) related to the news topic, with storyteller comments (written in "$language").
                6. "attachedJokeCulture": The specific folk style used for this joke.
                
                Format the output as a valid raw JSON array of objects. Each object MUST have these exact fields: "authorName", "authorHandle", "title", "content", "attachedJoke", "attachedJokeCulture".
                Do not wrap the output in markdown block tags like ```json. Just return raw JSON.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.8f, responseMimeType = "application/json"),
                tools = listOf(Tool(googleSearch = emptyMap()))
            )

            try {
                val response = GeminiApiClient.service.generateContentWithModel("gemini-3.8-flash", apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!responseText.isNullOrBlank()) {
                    val cleanText = cleanJson(responseText)
                    val jsonArray = JSONArray(cleanText)
                    val newList = mutableListOf<NewsDigest>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        newList.add(
                            NewsDigest(
                                title = obj.getString("title"),
                                content = obj.getString("content"),
                                attachedJoke = obj.getString("attachedJoke"),
                                attachedJokeCulture = obj.getString("attachedJokeCulture"),
                                authorName = obj.optString("authorName", ""),
                                authorHandle = obj.optString("authorHandle", "")
                            )
                        )
                    }

                    repository.updateNewsDigests(newList)
                    withContext(Dispatchers.Main) {
                        _newsLoading.value = false
                    }
                } else {
                    loadOfflineDigests("Empty news response.", topic = topic)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Gemini generate news failed", e)
                loadOfflineDigests("Network error: ${e.message}", topic = topic)
            }
        }
    }

    private suspend fun loadOfflineDigests(errorMsg: String? = null, topic: String? = null) {
        val isRussian = _selectedLanguage.value == "Russian" || _selectedLanguage.value == "Русский"
        val fallbacks = if (!topic.isNullOrBlank()) {
            listOf(
                NewsDigest(
                    title = if (isRussian) "Тренды: $topic" else "Trends: $topic",
                    content = if (isRussian) 
                        "Сводка актуальных трендов по теме «$topic» от источника AlphaSignalAI. Народный фольклор всегда доступен!"
                    else 
                        "Trends briefing on '$topic' from AlphaSignalAI. Folklore database is always up and running!",
                    attachedJoke = if (isRussian) 
                        "— Слышал, интернет отключили?\n— Да ты что! А как же мы теперь будем узнавать, хорошая ли погода на улице?\n— Ну не знаю, придется в окно посмотреть...\n— Да ну, бред какой-то, в окне же нет кнопки 'обновить'!"
                    else 
                        "An elder is asked: 'Why is the internet so slow today?'\nThe elder replies: 'Because the wisdom of generations travels on foot, not in fibers!'",
                    attachedJokeCulture = "Jewish",
                    authorName = "System",
                    authorHandle = "@system"
                )
            )
        } else {
            val baseList = if (isRussian) FallbackData.defaultNewsDigests else FallbackData.getEnglishNewsDigests()
            baseList.mapIndexed { index, d ->
                val handle = when (index % 5) {
                    0 -> "@TechCrunch"
                    1 -> "@Reuters"
                    2 -> "@NASA"
                    3 -> "@folklore"
                    else -> "@OdessaHumor"
                }
                val name = when (index % 5) {
                    0 -> "TechCrunch"
                    1 -> "Reuters"
                    2 -> "NASA"
                    3 -> "Folklore Observer"
                    else -> "Odessa Humor"
                }
                d.copy(authorName = name, authorHandle = handle)
            }
        }
        
        repository.updateNewsDigests(fallbacks)
        withContext(Dispatchers.Main) {
            _newsError.value = errorMsg
            _newsLoading.value = false
        }
    }

    // --- TTS (TEXT-TO-SPEECH) ACTIONS ---

    fun toggleTtsJoke(index: Int, text: String) {
        val id = "joke_$index"
        ttsManager.speakSingle(
            id = id,
            text = text,
            title = "Анекдот #$index",
            languageName = _selectedLanguage.value
        )
    }

    fun toggleTtsIntro() {
        val intro = _storytellerIntro.value
        if (intro.isNotBlank()) {
            ttsManager.speakSingle(
                id = "storyteller_intro",
                text = intro,
                title = "Вступление сказителя",
                languageName = _selectedLanguage.value
            )
        }
    }

    fun toggleTtsOutro() {
        val outro = _storytellerOutro.value
        if (outro.isNotBlank()) {
            ttsManager.speakSingle(
                id = "storyteller_outro",
                text = outro,
                title = "Напутствие сказителя",
                languageName = _selectedLanguage.value
            )
        }
    }

    fun toggleTtsSavedJoke(joke: SavedJoke) {
        val id = "saved_joke_${joke.id}"
        ttsManager.speakSingle(
            id = id,
            text = joke.jokeText,
            title = "Сохранённый анекдот (${joke.culture})",
            languageName = _selectedLanguage.value
        )
    }

    fun startFullStorytellerSession(startIndex: Int = 0) {
        ttsManager.startStorytellerSession(
            intro = _storytellerIntro.value,
            jokes = _storytellerJokes.value,
            outro = _storytellerOutro.value,
            languageName = _selectedLanguage.value,
            startIndex = startIndex
        )
    }

    fun pauseTts() = ttsManager.pause()
    fun resumeTts() = ttsManager.resume()
    fun nextTts() = ttsManager.next()
    fun prevTts() = ttsManager.previous()
    fun stopTts() = ttsManager.stop()
    fun cycleTtsRate() = ttsManager.cycleSpeechRate()
    fun setTtsVoicePreset(preset: String) = ttsManager.setVoicePreset(preset)

    // --- SPOTLIGHT ANECDOTE / WISDOM ---
    fun nextSpotlightJoke(maxSize: Int) {
        if (maxSize > 0) {
            _spotlightJokeIndex.value = (_spotlightJokeIndex.value + 1) % maxSize
        }
    }

    fun playSpotlightJoke(jokeText: String, culture: String) {
        val isRussian = _selectedLanguage.value == "Russian" || _selectedLanguage.value == "Русский"
        val title = if (isRussian) "Мудрость у костра ($culture)" else "Campfire Wisdom ($culture)"
        ttsManager.speakSingle("spotlight_joke", jokeText, title, _selectedLanguage.value)
    }

    // --- TRENDS TOPIC ACTIONS ---
    fun selectTrendTopic(topic: String?) {
        _selectedTrendTopic.value = topic
        generateXDigest(topic)
    }

    fun playAllTrendsDigests() {
        val digests = newsDigests.value
        if (digests.isEmpty()) return
        val isRussian = _selectedLanguage.value == "Russian" || _selectedLanguage.value == "Русский"
        val items = digests.mapIndexed { idx, d ->
            val narrationText = buildString {
                append(d.title)
                append(". ")
                append(d.content)
                if (d.attachedJoke.isNotBlank()) {
                    append(". ")
                    append(if (isRussian) "Культурный фольклор: " else "Cultural folklore: ")
                    append(d.attachedJoke)
                }
            }
            TtsNarratorItem(
                id = "trend_digest_$idx",
                text = narrationText,
                title = "${d.authorName}: ${d.title}",
                subtitle = if (isRussian) "Тренд #${idx + 1}" else "Trend #${idx + 1}"
            )
        }
        ttsManager.playCustomPlaylist(items, _selectedLanguage.value)
    }

    // --- TOP FAVORITES ACTIONS ---
    fun setTopCultureFilter(culture: String) {
        _selectedTopCulture.value = culture
    }

    fun playAllSavedJokes() {
        val jokes = savedJokes.value
        if (jokes.isEmpty()) return
        val isRussian = _selectedLanguage.value == "Russian" || _selectedLanguage.value == "Русский"
        val filter = _selectedTopCulture.value
        val filtered = if (filter == "all") jokes else jokes.filter { it.culture.lowercase() == filter.lowercase() }
        val toPlay = if (filtered.isNotEmpty()) filtered else jokes
        val items = toPlay.mapIndexed { idx, sj ->
            TtsNarratorItem(
                id = "fav_${sj.id}",
                text = sj.jokeText,
                title = if (isRussian) "ТОП: Анекдот #${idx + 1}" else "TOP: Joke #${idx + 1}",
                subtitle = sj.culture
            )
        }
        ttsManager.playCustomPlaylist(items, _selectedLanguage.value)
    }

    // --- MULTI-TURN GEMINI CHATBOT ACTIONS ---
    fun setChatModel(model: GeminiChatModel) {
        _chatModel.value = model
    }

    fun toggleChatSearchGrounding() {
        _chatUseSearchGrounding.value = !_chatUseSearchGrounding.value
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun narrateChatMessage(message: ChatMessage) {
        val title = if (message.sender == "user") "Вы" else "Мудрый сказитель (${message.modelUsed})"
        ttsManager.speakSingle("chat_${message.id}", message.text, title, _selectedLanguage.value)
    }

    fun narrateChatMessage(text: String, customTitle: String = "Мудрый сказитель (gemini-3.8-live)") {
        ttsManager.speakSingle("live_voice_replay", text, customTitle, _selectedLanguage.value)
    }

    fun sendMessageToElder(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _chatLoading.value) return

        val userMessage = ChatMessage(
            sender = "user",
            text = trimmed,
            modelUsed = AkynGeminiEngine.MODEL_NAME,
            isSearchGrounded = _chatUseSearchGrounding.value
        )
        val updatedList = _chatMessages.value + userMessage
        _chatMessages.value = updatedList
        _chatLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val language = _selectedLanguage.value
            val culture = _storytellerCulture.value
            val user = currentUser.value
            val userDisplayName = user?.displayName

            val history = updatedList.dropLast(1).takeLast(10).map { msg ->
                Pair(msg.sender, msg.text)
            }

            val result = akynEngine.chatReply(
                history = history,
                userMessage = trimmed,
                culture = culture,
                language = language,
                userDisplayName = userDisplayName
            )

            result.onSuccess { replyText ->
                withContext(Dispatchers.Main) {
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        sender = "model",
                        text = replyText,
                        modelUsed = AkynGeminiEngine.MODEL_NAME,
                        isSearchGrounded = false
                    )
                    _chatLoading.value = false
                }
                user?.uid?.let { uid ->
                    firestoreRepository.saveChatMessage(uid, "user", trimmed, culture)
                    firestoreRepository.saveChatMessage(uid, "akyn", replyText, culture)
                }
            }.onFailure { e ->
                Log.e("AppViewModel", "Akyn AI generation error", e)
                withContext(Dispatchers.Main) {
                    val isRussian = language == "Russian" || language == "Русский"
                    val friendName = userDisplayName ?: if (isRussian) "дорогой друг" else "my friend"
                    val fallback = if (isRussian) {
                        "Эх, $friendName! Акын бьёт по струнам домбры: «Что вижу — о том и пою! Вот вижу запрос твой про «$trimmed». Слушай быль: собрались однажды мудрец, купец и акын чай пить. Купец считает медяки, мудрец ищет глубокий смысл в чаинках, а акын уже смешит всю чайхану песней про то, как осёл соседа научился на рынке торговаться! Не грусти, жизнь — это веселая песня, если в ней есть звонкая струна и добрый смех!»"
                    } else {
                        "Ah, $friendName! The merry Akyn tunes his instrument: 'What I see — of that I sing! You spoke of: $trimmed. Once a wise man, a merchant, and an akyn met by the road. The merchant counted coins, the wise man searched for mysteries, but the akyn made everyone burst into laughter with a song about a goat negotiating for cabbage! Keep smiling, my friend!'"
                    }
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        sender = "model",
                        text = fallback,
                        modelUsed = AkynGeminiEngine.MODEL_NAME
                    )
                    _chatLoading.value = false
                }
            }
        }
    }

    // --- LIVE VOICE CONVERSATIONS (gemini-3.8-live) ACTIONS ---
    fun startLiveVoiceSession() {
        _liveVoiceState.value = LiveVoiceState(
            isActive = true,
            isListening = true,
            currentTranscription = "",
            elderResponseText = ""
        )
    }

    fun stopLiveVoiceSession() {
        ttsManager.stop()
        _liveVoiceState.value = LiveVoiceState(isActive = false)
    }

    fun sendLiveVoiceQuery(spokenText: String) {
        if (spokenText.isBlank()) return
        _liveVoiceState.value = _liveVoiceState.value.copy(
            isListening = false,
            isProcessing = true,
            currentTranscription = spokenText,
            isElderSpeaking = false,
            errorMessage = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            val language = _selectedLanguage.value
            val culture = _storytellerCulture.value

            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                delay(400)
                val fallback = if (language == "Russian" || language == "Русский")
                    "Слышу голос твой, путник! Наш костёр горит ярко, а сердце полно народной радости и мудрости!"
                else
                    "I hear your voice clearly, traveler! Our campfire burns bright with folk warmth and wisdom!"
                withContext(Dispatchers.Main) {
                    _liveVoiceState.value = _liveVoiceState.value.copy(
                        elderResponseText = fallback,
                        isElderSpeaking = true,
                        isProcessing = false
                    )
                    ttsManager.setVoicePreset("elder")
                    ttsManager.speakSingle("live_voice_elder", fallback, "Мудрец у костра (Live)", language)
                }
                return@launch
            }

            val prompt = """
                You are in a live real-time voice conversation as the Wise Folk Elder and Storyteller ($culture style).
                The user just spoke to you: "$spokenText".
                Provide a warm, snappy, wise, and humorous 2-3 sentence conversational spoken reply in "$language".
                Speak naturally as if talking right across the campfire with authentic folk charm and humor!
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.9f)
            )

            try {
                val response = GeminiApiClient.service.generateContentWithModel("gemini-3.8-live", apiKey, request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Хорошо сказано, путник! Главное в пути — не терять улыбку и доброе слово!"

                withContext(Dispatchers.Main) {
                    _liveVoiceState.value = _liveVoiceState.value.copy(
                        elderResponseText = reply,
                        isElderSpeaking = true,
                        isProcessing = false
                    )
                    ttsManager.setVoicePreset("elder")
                    ttsManager.speakSingle("live_voice_elder", reply, "Мудрец у костра (Live API)", language)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Live API error", e)
                withContext(Dispatchers.Main) {
                    val fallback = if (language == "Russian" || language == "Русский")
                        "Слышу твой голос, путник! Главное — не унывать и делиться радостью!"
                    else
                        "I hear your voice, traveler! Keep smiling and sharing the joy!"
                    _liveVoiceState.value = _liveVoiceState.value.copy(
                        elderResponseText = fallback,
                        isElderSpeaking = true,
                        isProcessing = false
                    )
                    ttsManager.speakSingle("live_voice_elder", fallback, "Мудрец у костра (Live)", language)
                }
            }
        }
    }

    // --- CAMPFIRE AMBIENCE ACTIONS ---
    fun toggleCampfire() = campfirePlayer.toggle()
    fun setCampfireVolume(vol: Float) = campfirePlayer.setVolume(vol)

    // --- SLEEP TIMER ACTIONS ---
    fun setSleepTimerMinutes(minutes: Int) = radioManager.setSleepTimerMinutes(minutes)
    fun cancelSleepTimer() = radioManager.cancelSleepTimer()

    // --- EXPORT SAVED JOKES ---
    fun formatAllSavedJokesForExport(): String {
        val jokes = savedJokes.value
        if (jokes.isEmpty()) return ""
        val sb = StringBuilder()
        sb.append("📜 Золотая антология народных анекдотов\n")
        sb.append("═══════════════════════════════════════\n\n")
        jokes.forEachIndexed { i, joke ->
            sb.append("${i + 1}. [${joke.culture}]")
            if (joke.situation.isNotBlank()) {
                sb.append(" (Тема: ${joke.situation})")
            }
            sb.append("\n${joke.jokeText}\n\n")
        }
        sb.append("═══════════════════════════════════════\n")
        sb.append("Собрано в приложении «Folk Anecdotes & Radio»\n")
        return sb.toString()
    }

    override fun onCleared() {
        super.onCleared()
        campfirePlayer.stop()
        radioManager.cancelSleepTimer()
        ttsManager.shutdown()
    }
}
