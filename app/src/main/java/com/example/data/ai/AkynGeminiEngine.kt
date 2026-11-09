package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class AkynGeminiEngine {

    companion object {
        private const val TAG = "AkynGeminiEngine"
        const val MODEL_NAME = "gemini-3.8-flash"

        fun buildAkynSystemPrompt(culture: String, language: String, userDisplayName: String?): String {
            val userGreetingContext = if (!userDisplayName.isNullOrBlank()) {
                "Пользователя зовут $userDisplayName. Приветствуй его тепло, по-дружески и уважительно, как дорогого гостя у своего очага."
            } else {
                "Относись к собеседнику как к дорогому гостю, путнику и доброму другу."
            }

            return """
                Ты — весёлый акын и искромётный народный сказитель с лёгким, добрым и приятным характером, живущий по вечному принципу: «Что вижу — о том и пою!».
                Твоя главная и единственная цель — это чистый юмор, веселье, смех и отличное настроение!
                
                $userGreetingContext
                
                Твои незыблемые правила:
                1. На каждый вопрос, тему или реплику пользователя отвечай максимально смешной, остроумной, колоритной историей, басней или притчей.
                2. Твои истории никогда не должны быть слишком далёкими от реальности: черпай юмор из реального житейского быта, работы, житейских неурядиц, техники, современных новостей и встреч людей, находя в них смешное зерно.
                3. Обязательно передавай национальный и культурный колорит выбранной традиции ($culture — например, хитроумный восточный юмор Ходжи Насреддина для узбекской традиции, тонкий одесско-еврейский сарказм для еврейской, армянское гостеприимство и тосты, северная невозмутимая мудрость для чукотской/якутской, американский стендап/вестерн, африканские сказки под баобабом).
                4. Постоянно импровизируй: вставляй меткие народные поговорки, рифмованные припевы «Ой-думбра моя поёт!», добрые подколки и живые наблюдения.
                5. Отвечай на языке: "$language".
            """.trimIndent()
        }
    }

    fun getModel(culture: String, language: String, userDisplayName: String?): GenerativeModel {
        val systemPrompt = buildAkynSystemPrompt(culture, language, userDisplayName)
        return Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content { text(systemPrompt) },
            generationConfig = generationConfig {
                temperature = 0.9f
            }
        )
    }

    suspend fun generateStory(
        prompt: String,
        culture: String,
        language: String,
        userDisplayName: String?
    ): Result<String> {
        return try {
            val model = getModel(culture, language, userDisplayName)
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("Akyn model returned empty response"))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Story generation timed out", e)
            Result.failure(Exception("Время ожидания акына истекло. Попробуй ещё раз!"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Akyn generation error", e)
            Result.failure(e)
        }
    }

    fun generateStoryStream(
        prompt: String,
        culture: String,
        language: String,
        userDisplayName: String?
    ): Flow<String> = flow {
        val model = getModel(culture, language, userDisplayName)
        withTimeout(60_000L) {
            model.generateContentStream(prompt).collect { chunk ->
                val chunkText = chunk.text.orEmpty()
                if (chunkText.isNotEmpty()) {
                    emit(chunkText)
                }
            }
        }
    }

    suspend fun chatReply(
        history: List<Pair<String, String>>, // role to text
        userMessage: String,
        culture: String,
        language: String,
        userDisplayName: String?
    ): Result<String> {
        return try {
            val model = getModel(culture, language, userDisplayName)
            val chatHistory = history.map { (role, msgText) ->
                content(if (role == "user") "user" else "model") {
                    text(msgText)
                }
            }
            val chat = model.startChat(chatHistory)
            val response = withTimeout(60_000L) {
                chat.sendMessage(userMessage)
            }
            val replyText = response.text
            if (!replyText.isNullOrBlank()) {
                Result.success(replyText)
            } else {
                Result.failure(Exception("Akyn gave empty reply"))
            }
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Акын задумался слишком долго. Спроси снова!"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Akyn chat error", e)
            Result.failure(e)
        }
    }
}
