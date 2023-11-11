package com.example.secrethitler.ui.players


import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.secrethitler.data.PlayersPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersPreferencesRepository: PlayersPreferencesRepository
) : ViewModel() {

    val initialSetupEvent = liveData {
        emit(playersPreferencesRepository.fetchInitialPreferences())
    }

    private val playerPreferencesFlow = playersPreferencesRepository.playerPreferencesFlow

    fun updatePlayersList(newList: List<String>) {
        viewModelScope.launch {
            playersPreferencesRepository.updatePlayers(newList)
        }
    }

}

