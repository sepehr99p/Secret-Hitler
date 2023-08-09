package com.example.secrethitler.data

import android.util.Log
import androidx.datastore.core.DataStore
import com.codelab.android.datastore.PlayerPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class PlayersPreferencesRepository(private val playerPreferencesDataStore: DataStore<PlayerPreferences>) {

    private val TAG: String = "PlayerPreferencesRepo"

    val playerPreferencesFlow: Flow<PlayerPreferences> = playerPreferencesDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(PlayerPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updatePlayers(newNames : List<String>) {
        playerPreferencesDataStore.updateData {
            it.toBuilder()
                .clearNames()
                .addAllNames(newNames)
                .build()
        }
    }

    suspend fun fetchInitialPreferences() = playerPreferencesDataStore.data.first()
}