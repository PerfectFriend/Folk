package com.example

import com.example.data.audio.CampfireAmbiencePlayer
import com.example.data.database.SavedJoke
import com.example.data.radio.RadioManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EvolutionFeaturesTest {

    @Test
    fun testSleepTimerSettingAndCancellation() {
        val radioManager = RadioManager.getInstance()

        radioManager.setSleepTimerMinutes(15)
        assertEquals(15 * 60, radioManager.sleepTimerRemainingSeconds.value)

        radioManager.setSleepTimerMinutes(30)
        assertEquals(30 * 60, radioManager.sleepTimerRemainingSeconds.value)

        radioManager.cancelSleepTimer()
        assertEquals(0, radioManager.sleepTimerRemainingSeconds.value)
    }

    @Test
    fun testCampfireAmbienceControls() {
        val campfirePlayer = CampfireAmbiencePlayer()
        assertFalse(campfirePlayer.isPlaying.value)

        campfirePlayer.setVolume(0.8f)
        assertEquals(0.8f, campfirePlayer.volume.value, 0.01f)

        // Clamping check
        campfirePlayer.setVolume(1.5f)
        assertEquals(1.0f, campfirePlayer.volume.value, 0.01f)

        campfirePlayer.setVolume(-0.5f)
        assertEquals(0.0f, campfirePlayer.volume.value, 0.01f)
    }

    @Test
    fun testExportAnthologyFormatting() {
        val testJokes = listOf(
            SavedJoke(id = 1, jokeText = "Первый смешной анекдот", culture = "Uzbek", situation = "Чайхана"),
            SavedJoke(id = 2, jokeText = "Второй смешной анекдот", culture = "Jewish", situation = "Одесса")
        )

        val sb = StringBuilder()
        sb.append("📜 Золотая антология народных анекдотов\n")
        sb.append("═══════════════════════════════════════\n\n")
        testJokes.forEachIndexed { i, joke ->
            sb.append("${i + 1}. [${joke.culture}]")
            if (joke.situation.isNotBlank()) {
                sb.append(" (Тема: ${joke.situation})")
            }
            sb.append("\n${joke.jokeText}\n\n")
        }
        val formatted = sb.toString()

        assertTrue(formatted.contains("Золотая антология"))
        assertTrue(formatted.contains("Первый смешной анекдот"))
        assertTrue(formatted.contains("[Jewish] (Тема: Одесса)"))
    }

    @Test
    fun testRadioVolumeControlsAndMuteToggle() {
        val radioManager = RadioManager.getInstance()
        radioManager.setVolume(0.75f)
        assertEquals(0.75f, radioManager.volume.value, 0.01f)

        // Test clamping
        radioManager.setVolume(1.4f)
        assertEquals(1.0f, radioManager.volume.value, 0.01f)

        radioManager.setVolume(-0.2f)
        assertEquals(0.0f, radioManager.volume.value, 0.01f)

        // Set to 0.6f and test toggleMute
        radioManager.setVolume(0.6f)
        assertEquals(0.6f, radioManager.volume.value, 0.01f)

        // Mute
        radioManager.toggleMute()
        assertEquals(0.0f, radioManager.volume.value, 0.01f)

        // Unmute: restores previous 0.6f
        radioManager.toggleMute()
        assertEquals(0.6f, radioManager.volume.value, 0.01f)
    }

    @Test
    fun testGeminiModelIdentifiers() {
        assertEquals("gemini-3.8-flash", com.example.data.chat.GeminiChatModel.FLASH.modelId)
        assertEquals("gemini-4-argon", com.example.data.chat.GeminiChatModel.ARGON.modelId)
        assertEquals("gemini-3.1-flash-lite", com.example.data.chat.GeminiChatModel.FLASH_LITE.modelId)
    }

    @Test
    fun testChatMessageCreation() {
        val userMsg = com.example.data.chat.ChatMessage(
            role = "user",
            content = "Расскажи притчу"
        )
        val modelMsg = com.example.data.chat.ChatMessage(
            role = "model",
            content = "Жил-был старец...",
            modelUsed = "gemini-4-argon",
            isSearchGrounded = true
        )

        assertEquals("user", userMsg.role)
        assertEquals("model", modelMsg.role)
        assertEquals("gemini-4-argon", modelMsg.modelUsed)
        assertTrue(modelMsg.isSearchGrounded)
        assertFalse(userMsg.isSearchGrounded)
    }

    @Test
    fun testLiveVoiceStateInitialization() {
        val defaultState = com.example.data.chat.LiveVoiceState()
        assertFalse(defaultState.isConnected)
        assertFalse(defaultState.isListening)
        assertFalse(defaultState.isSpeaking)
        assertFalse(defaultState.isProcessing)
        assertEquals("", defaultState.lastUserSpokenText)
        assertEquals("", defaultState.lastModelSpokenResponse)
    }

    @Test
    fun testRadioPlayerServiceActions() {
        assertEquals("com.example.radio.ACTION_PLAY", com.example.data.radio.RadioPlayerService.ACTION_PLAY)
        assertEquals("com.example.radio.ACTION_PAUSE", com.example.data.radio.RadioPlayerService.ACTION_PAUSE)
        assertEquals("com.example.radio.ACTION_RESUME", com.example.data.radio.RadioPlayerService.ACTION_RESUME)
        assertEquals("com.example.radio.ACTION_TOGGLE", com.example.data.radio.RadioPlayerService.ACTION_TOGGLE)
        assertEquals("com.example.radio.ACTION_STOP", com.example.data.radio.RadioPlayerService.ACTION_STOP)
        assertEquals("com.example.radio.ACTION_SET_VOLUME", com.example.data.radio.RadioPlayerService.ACTION_SET_VOLUME)
    }

    @Test
    fun testRadioManagerNotificationBridge() {
        val radioManager = com.example.data.radio.RadioManager.getInstance()
        radioManager.notifyLoading("http://test.stream/live")
        assertTrue(radioManager.isLoading.value)
        assertFalse(radioManager.isPlaying.value)
        assertEquals("http://test.stream/live", radioManager.currentStreamUrl.value)

        radioManager.notifyPlaying(true)
        assertTrue(radioManager.isPlaying.value)
        assertFalse(radioManager.isLoading.value)

        radioManager.notifyPlaying(false)
        assertFalse(radioManager.isPlaying.value)

        radioManager.notifyError("Failed buffer")
        assertEquals("Failed buffer", radioManager.error.value)
    }

    @Test
    fun testDailyCulturalRegionalStories() {
        val cultures = listOf("Uzbek", "Armenian", "Jewish", "Chukchi", "Yakut", "Kamchatka", "American", "African")
        for (culture in cultures) {
            val russianJokes = com.example.data.FallbackData.getFallbackJokes(culture, true)
            val englishJokes = com.example.data.FallbackData.getFallbackJokes(culture, false)
            assertTrue("Expected stories for culture $culture in Russian", russianJokes.isNotEmpty())
            assertTrue("Expected stories for culture $culture in English", englishJokes.isNotEmpty())
            assertTrue("Story content should not be blank", russianJokes[0].isNotBlank())
        }

        val sampleStory = com.example.ui.components.RegionalStory(
            id = "test_story",
            cultureKey = "Uzbek",
            cultureName = "Узбекский",
            flagEmoji = "🇺🇿",
            regionTag = "Шелковый путь",
            text = "Насреддин и бай",
            moral = "Мудрость вечна"
        )
        assertEquals("test_story", sampleStory.id)
        assertEquals("Uzbek", sampleStory.cultureKey)
        assertEquals("🇺🇿", sampleStory.flagEmoji)
        assertEquals("Мудрость вечна", sampleStory.moral)
    }
}


