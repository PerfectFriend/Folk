package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

@IgnoreExtraProperties
data class FirestoreUserProfile(
    val selectedCulture: String = "Uzbek",
    val selectedLanguage: String = "Russian",
    val displayName: String? = null,
    val email: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreSavedJoke(
    val jokeText: String = "",
    val culture: String = "",
    val authorName: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

class AkynFirestoreRepository(
    private val db: FirebaseFirestore
) {
    companion object {
        private const val TAG = "AkynFirestoreRepo"

        fun create(context: Context): AkynFirestoreRepository {
            val databaseId = context.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(databaseId)
            return AkynFirestoreRepository(firestore)
        }
    }

    suspend fun saveUserProfile(
        userId: String,
        culture: String,
        language: String,
        displayName: String?,
        email: String?
    ) {
        try {
            val userDoc = db.collection("users").document(userId)
            val data = mutableMapOf<String, Any>(
                "selectedCulture" to culture,
                "selectedLanguage" to language,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!displayName.isNullOrBlank()) {
                data["displayName"] = displayName
            }
            if (!email.isNullOrBlank()) {
                data["email"] = email
            }
            userDoc.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
            Log.d(TAG, "Saved profile for user $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile in Firestore", e)
        }
    }

    suspend fun saveJoke(
        userId: String,
        jokeText: String,
        culture: String,
        authorName: String?
    ): Boolean {
        return try {
            val jokesCol = db.collection("users").document(userId).collection("saved_jokes")
            val jokeData = mutableMapOf<String, Any>(
                "jokeText" to jokeText,
                "culture" to culture,
                "createdAt" to FieldValue.serverTimestamp()
            )
            if (!authorName.isNullOrBlank()) {
                jokeData["authorName"] = authorName
            }
            jokesCol.add(jokeData).await()
            Log.d(TAG, "Joke saved to Firestore for user $userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving joke to Firestore", e)
            false
        }
    }

    suspend fun saveChatMessage(
        userId: String,
        sender: String,
        text: String,
        culture: String
    ) {
        try {
            val chatCol = db.collection("users").document(userId).collection("chat_history")
            val chatData = mapOf(
                "sender" to sender,
                "text" to text,
                "culture" to culture,
                "createdAt" to FieldValue.serverTimestamp()
            )
            chatCol.add(chatData).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving chat message to Firestore", e)
        }
    }
}
