package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.tts.TtsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TtsManagerTest {

    @Test
    fun testTtsLanguageLocaleMapping() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = TtsManager(context)

        assertEquals(Locale.forLanguageTag("ru-RU"), manager.getLocaleForLanguage("Russian"))
        assertEquals(Locale.forLanguageTag("ru-RU"), manager.getLocaleForLanguage("Русский"))
        assertEquals(Locale.US, manager.getLocaleForLanguage("English"))
        assertEquals(Locale.forLanguageTag("es-ES"), manager.getLocaleForLanguage("Spanish"))
        assertEquals(Locale.SIMPLIFIED_CHINESE, manager.getLocaleForLanguage("Chinese"))
        assertEquals(Locale.GERMAN, manager.getLocaleForLanguage("German"))
        assertEquals(Locale.FRENCH, manager.getLocaleForLanguage("French"))
    }

    @Test
    fun testVoicePresetSwitching() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = TtsManager(context)

        manager.setVoicePreset("elder")
        assertEquals("elder", manager.voicePreset.value)
        assertEquals(0.82f, manager.pitch.value, 0.01f)

        manager.setVoicePreset("cheerful")
        assertEquals("cheerful", manager.voicePreset.value)
        assertEquals(1.18f, manager.pitch.value, 0.01f)

        manager.setVoicePreset("classic")
        assertEquals("classic", manager.voicePreset.value)
        assertEquals(1.0f, manager.pitch.value, 0.01f)
    }

    @Test
    fun testSpeechRateCycle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = TtsManager(context)

        assertEquals(1.0f, manager.speechRate.value, 0.01f)
        manager.cycleSpeechRate()
        assertEquals(1.25f, manager.speechRate.value, 0.01f)
        manager.cycleSpeechRate()
        assertEquals(1.5f, manager.speechRate.value, 0.01f)
        manager.cycleSpeechRate()
        assertEquals(0.85f, manager.speechRate.value, 0.01f)
        manager.cycleSpeechRate()
        assertEquals(1.0f, manager.speechRate.value, 0.01f)
    }
}
