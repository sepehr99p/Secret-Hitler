package com.example.secrethitler.ui.players


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.secrethitler.data.PlayersPreferencesRepository
import kotlinx.coroutines.launch

data class PlayersUiModel(
    val players: List<String>
)


class PlayersViewModel(
    private val playersPreferencesRepository: PlayersPreferencesRepository
) : ViewModel() {

    val initialSetupEvent = liveData {
        emit(playersPreferencesRepository.fetchInitialPreferences())
    }

    private val playerPreferencesFlow = playersPreferencesRepository.playerPreferencesFlow

    fun updatePlayersList(newList : List<String>) {
        viewModelScope.launch {
            playersPreferencesRepository.updatePlayers(newList)
        }
    }

}

class PlayersViewModelFactory(
    private val playersPreferencesRepository: PlayersPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayersViewModel(playersPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
