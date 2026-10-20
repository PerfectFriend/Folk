package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.BuildConfig
import com.example.data.FallbackData
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
import com.example.data.radio.RadioManager
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

    // News Digest state
    private val _newsLoading = MutableStateFlow(false)
    val newsLoading: StateFlow<Boolean> = _newsLoading.asStateFlow()

    private val _newsError = MutableStateFlow<String?>(null)
    val newsError: StateFlow<String?> = _newsError.asStateFlow()

    // X Accounts state
    private val _monitoredAccounts = MutableStateFlow<List<String>>(
        listOf("@NASA", "@TechCrunch", "@Reuters", "@folklore", "@OdessaHumor")
    )
    val monitoredAccounts: StateFlow<List<String>> = _monitoredAccounts.asStateFlow()

    private val _selectedAccounts = MutableStateFlow<Set<String>>(
        setOf("@NASA", "@TechCrunch", "@Reuters", "@folklore", "@OdessaHumor")
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

    init {
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
                    repository.updateNewsDigests(mapped)
                }
            }
        }
    }

    fun selectLanguage(lang: String) {
        _selectedLanguage.value = lang
        sharedPrefs.edit().putString("language", lang).apply()
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
            radioManager.playStream(station.streamUrl)
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
            radioManager.togglePlayPause()
        }
    }

    fun stopRadio() {
        if (_useHtml5Player.value) {
            _isRadioPlaying.value = false
            _isRadioLoading.value = false
            _currentRadioUrl.value = null
        } else {
            radioManager.stopStream()
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
        radioManager.setVolume(vol)
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
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback to local offline jokes
                loadOfflineJokes(culture)
                return@launch
            }

            val prompt = """
                You are a genial folk storyteller who has 20 jokes for any situation!
                We are in a situation: "$situation".
                Please tell exactly 20 short, hilarious jokes or cultural funny stories in the style of "$culture" folklore (including characteristic folk idioms, starting phrases, and unique sense of humor, for example Uzbek jokes about Nasreddin, Jewish jokes about Odessa, Chukchi/Yakut/Kamchatka jokes about tundra/nature/bears, Armenian jokes about Armenian Radio, American cowboy jokes, African savannah animal jokes).
                All output MUST be written in the following language: "$language".
                Format the output as a clean, structured JSON format with:
                - "storytellerIntro": A funny introductory remark from you as the storyteller in the selected language (e.g. "Ah, sit down, my dear friends, and listen to these stories...").
                - "jokes": A JSON array of exactly 20 strings. Each string is a complete short joke, well-formatted with paragraph breaks.
                - "storytellerOutro": A funny closing remark from you as the storyteller.
                
                Return ONLY a valid raw JSON object. Do not wrap it in markdown block tags like ```json or anything. Just raw JSON text.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.85f, responseMimeType = "application/json")
            )

            try {
                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!responseText.isNullOrBlank()) {
                    val cleanText = cleanJson(responseText)
                    val json = JSONObject(cleanText)
                    val intro = json.optString("storytellerIntro", "Listen up, my friends!")
                    val outro = json.optString("storytellerOutro", "That's all for now, haha!")
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
                } else {
                    loadOfflineJokes(culture, "Empty response from AI.")
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Gemini generate content failed", e)
                loadOfflineJokes(culture, "Network error: ${e.message}")
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
                You are integrated with X.com (Twitter). Your task is to fetch/generate recent posts from the following specific X accounts: $accountsString.
                Generate exactly 5 news digest items/posts $topicFocusPrompt.
                For each item, you MUST provide:
                1. "authorName": The display name of the account (e.g. "NASA" for @NASA, "TechCrunch" for @TechCrunch, etc. If it is a custom account handle, make up a fitting real name).
                2. "authorHandle": The exact Twitter handle including the @ symbol (e.g., "@NASA"). It MUST be one of the accounts requested in the list: $accountsString.
                3. "title": A catchy headline for the post (written in "$language").
                4. "content": A 2-3 sentence tweet or news update text representing the account's recent activity (written in "$language").
                5. "attachedJoke": A highly fitting folklore joke (Uzbek, Armenian, Jewish, Chukchi, Yakut, Kamchatka, American, or African style) related to the news topic, with storyteller comments (written in "$language").
                6. "attachedJokeCulture": The specific folk style used for this joke.
                
                Format the output as a valid raw JSON array of objects. Each object MUST have these exact fields: "authorName", "authorHandle", "title", "content", "attachedJoke", "attachedJokeCulture".
                Do not wrap the output in markdown block tags like ```json. Just return raw JSON.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.8f, responseMimeType = "application/json")
            )

            try {
                val response = GeminiApiClient.service.generateContent(apiKey, request)
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
                    title = if (isRussian) "Поиск по теме: $topic" else "X.com Search: $topic",
                    content = if (isRussian) 
                        "К сожалению, сейчас мы офлайн и не можем получить свежие твиты по запросу «$topic». Но наши народные мудрецы всегда на связи!"
                    else 
                        "We are currently offline and cannot query live X.com feeds for '$topic'. However, our folklore database is always up and running!",
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
}
