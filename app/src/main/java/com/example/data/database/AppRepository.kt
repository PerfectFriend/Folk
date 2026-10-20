package com.example.data.database

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val savedJokes: Flow<List<SavedJoke>> = appDao.getSavedJokes()
    val radioStations: Flow<List<RadioStation>> = appDao.getRadioStations()
    val newsDigests: Flow<List<NewsDigest>> = appDao.getNewsDigests()

    suspend fun insertJoke(joke: SavedJoke) = appDao.insertJoke(joke)
    suspend fun deleteJokeById(id: Int) = appDao.deleteJokeById(id)
    suspend fun isJokeSaved(text: String): Boolean = appDao.isJokeSaved(text)
    suspend fun deleteJokeByText(text: String) = appDao.deleteJokeByText(text)

    suspend fun insertRadioStation(station: RadioStation) = appDao.insertRadioStation(station)
    suspend fun insertRadioStations(stations: List<RadioStation>) = appDao.insertRadioStations(stations)
    suspend fun updateRadioFavorite(id: Int, isFav: Boolean) = appDao.updateRadioFavorite(id, isFav)
    suspend fun deleteRadioStation(id: Int) = appDao.deleteRadioStation(id)

    suspend fun updateNewsDigests(digests: List<NewsDigest>) {
        appDao.clearNewsDigests()
        appDao.insertNewsDigests(digests)
    }
}
