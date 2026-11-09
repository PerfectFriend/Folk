package com.example.data.chat

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String = "user", // "user" or "model"
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "gemini-3.8-flash",
    val isSearchGrounded: Boolean = false,
    val searchSources: List<String> = emptyList()
) {
    constructor(
        role: String,
        content: String,
        modelUsed: String = "gemini-3.8-flash",
        isSearchGrounded: Boolean = false,
        searchSources: List<String> = emptyList()
    ) : this(
        id = UUID.randomUUID().toString(),
        sender = role,
        text = content,
        timestamp = System.currentTimeMillis(),
        modelUsed = modelUsed,
        isSearchGrounded = isSearchGrounded,
        searchSources = searchSources
    )

    val role: String get() = sender
    val content: String get() = text
}

enum class GeminiChatModel(val modelId: String, val displayName: String, val description: String) {
    FLASH("gemini-3.8-flash", "Gemini 3.8 Flash", "Сбалансированный (общие задачи)"),
    ARGON("gemini-4-argon", "Gemini 4 Argon", "Глубокие рассуждения и сложная мудрость"),
    FLASH_LITE("gemini-3.1-flash-lite", "Gemini 3.1 Flash-Lite", "Молниеносный быстрый ответ")
}

data class LiveVoiceState(
    val isActive: Boolean = false,
    val isListening: Boolean = false,
    val isElderSpeaking: Boolean = false,
    val currentTranscription: String = "",
    val elderResponseText: String = "",
    val errorMessage: String? = null,
    val isProcessing: Boolean = false
) {
    val isSpeaking: Boolean get() = isElderSpeaking
    val isConnected: Boolean get() = isActive
    val lastUserSpokenText: String get() = currentTranscription
    val lastModelSpokenResponse: String get() = elderResponseText
}
