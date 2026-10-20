package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_jokes")
data class SavedJoke(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jokeText: String,
    val culture: String,
    val situation: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "radio_stations")
data class RadioStation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val streamUrl: String,
    val isFavorite: Boolean = false,
    val isDefault: Boolean = false
)

@Entity(tableName = "news_digests")
data class NewsDigest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val attachedJoke: String,
    val attachedJokeCulture: String,
    val timestamp: Long = System.currentTimeMillis(),
    val authorName: String = "",
    val authorHandle: String = ""
)

@Dao
interface AppDao {
    // Saved Jokes
    @Query("SELECT * FROM saved_jokes ORDER BY timestamp DESC")
    fun getSavedJokes(): Flow<List<SavedJoke>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoke(joke: SavedJoke)

    @Query("DELETE FROM saved_jokes WHERE id = :id")
    suspend fun deleteJokeById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_jokes WHERE jokeText = :text LIMIT 1)")
    suspend fun isJokeSaved(text: String): Boolean

    @Query("DELETE FROM saved_jokes WHERE jokeText = :text")
    suspend fun deleteJokeByText(text: String)

    // Radio Stations
    @Query("SELECT * FROM radio_stations")
    fun getRadioStations(): Flow<List<RadioStation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadioStation(station: RadioStation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadioStations(stations: List<RadioStation>)

    @Query("UPDATE radio_stations SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateRadioFavorite(id: Int, isFav: Boolean)

    @Query("DELETE FROM radio_stations WHERE id = :id")
    suspend fun deleteRadioStation(id: Int)

    // News Digests
    @Query("SELECT * FROM news_digests ORDER BY timestamp DESC")
    fun getNewsDigests(): Flow<List<NewsDigest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsDigests(digests: List<NewsDigest>)

    @Query("DELETE FROM news_digests")
    suspend fun clearNewsDigests()
}

@Database(entities = [SavedJoke::class, RadioStation::class, NewsDigest::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
