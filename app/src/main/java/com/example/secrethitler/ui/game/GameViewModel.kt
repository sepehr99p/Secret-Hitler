package com.example.secrethitler.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.PlayersPreferencesRepository
import com.example.secrethitler.data.ROLE
import java.util.Stack
import kotlin.random.Random

class GameViewModel(
    private val playersPreferencesRepository: PlayersPreferencesRepository
) : ViewModel() {

    var fascismCount = 0
    var liberalsCount = 0
    var hitlerCount = 1
    var communismCount = 0
    var stalinCount = 0
    var presidentWatchCount = 1

    private val laws by lazy { Stack<GameFragment.LAW>() }
    val trashLaws = arrayListOf<GameFragment.LAW>()
    val liberalSubmittedLaw = arrayListOf<GameFragment.LAW>()
    val fascismSubmittedLaw = arrayListOf<GameFragment.LAW>()
    var currentPlayerIndex = 0
    val gamePlayers = arrayListOf<Player>()


    private val _whoWon = MutableLiveData<ROLE>()
    val whoWon : LiveData<ROLE> get() = _whoWon


    val initialSetupEvent = liveData {
        emit(playersPreferencesRepository.fetchInitialPreferences())
    }

    fun initLaws() {
        repeat(6) {
            trashLaws.add(GameFragment.LAW.LIBERAL)
        }
        repeat(11) {
            trashLaws.add(GameFragment.LAW.FASCISM)
        }
        trashLaws.shuffle(Random(System.currentTimeMillis()))
        laws.clear()
        laws.addAll(trashLaws)
        trashLaws.clear()
    }

    fun submitLaw(last: GameFragment.LAW) {
        if (last.name == GameFragment.LAW.LIBERAL.name) {
            liberalSubmittedLaw.add(last)
        } else {
            fascismSubmittedLaw.add(last)
        }
        checkGameResult()
    }

    private fun checkGameResult() {
        if (liberalSubmittedLaw.size == 5 ) {
            _whoWon.value = ROLE.LIBERAL
        } else if (fascismSubmittedLaw.size ==6) {
            _whoWon.value = ROLE.FASCISM
        }
    }

    fun getLaw(): GameFragment.LAW {
        if (laws.isEmpty()) {
            trashLaws.shuffle(Random(System.currentTimeMillis()))
            laws.addAll(trashLaws)
            trashLaws.clear()
        }
        return laws.pop()
    }

    fun getPlayerRoleText(): String = gamePlayers[currentPlayerIndex].role.toString()
    fun getPlayerRoleColor(): Int = gamePlayers[currentPlayerIndex].role.color


}

class GameViewModelFactory(
    private val playersPreferencesRepository: PlayersPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(playersPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
