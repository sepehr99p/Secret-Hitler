package com.example.secrethitler.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.secrethitler.data.LAW
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

    private val laws by lazy { Stack<LAW>() }
    val trashLaws = arrayListOf<LAW>()
    val liberalSubmittedLaw = arrayListOf<LAW>()
    val fascismSubmittedLaw = arrayListOf<LAW>()
    val communismSubmittedLaw = arrayListOf<LAW>()
    var currentPlayerIndex = 0
    val gamePlayers = arrayListOf<Player>()


    private val _whoWon = MutableLiveData<ROLE>()
    val whoWon : LiveData<ROLE> get() = _whoWon


    val initialSetupEvent = liveData {
        emit(playersPreferencesRepository.fetchInitialPreferences())
    }

    fun initLaws() {
        repeat(6) {
            trashLaws.add(LAW.LIBERAL)
        }
        repeat(11) {
            trashLaws.add(LAW.FASCISM)
        }
        if (gamePlayers.size > 10) {
            repeat(2) {
                trashLaws.add(LAW.COMMUNISM)
            }
        }
        trashLaws.shuffle(Random(System.currentTimeMillis()))
        laws.clear()
        laws.addAll(trashLaws)
        trashLaws.clear()
    }

    fun submitLaw(last: LAW) {
        if (last.name == LAW.LIBERAL.name) {
            liberalSubmittedLaw.add(last)
        } else if(last.name == LAW.FASCISM.name) {
            fascismSubmittedLaw.add(last)
        } else {
            communismSubmittedLaw.add(last)
        }
        checkGameResult()
    }

    private fun checkGameResult() {
        if (liberalSubmittedLaw.size == 5 ) {
            _whoWon.value = ROLE.LIBERAL
        } else if (fascismSubmittedLaw.size ==6) {
            _whoWon.value = ROLE.FASCISM
        } else if (communismSubmittedLaw.size == 2 ) {
            _whoWon.value = ROLE.COMMUNISM
        }
    }

    fun getLaw(): LAW {
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
