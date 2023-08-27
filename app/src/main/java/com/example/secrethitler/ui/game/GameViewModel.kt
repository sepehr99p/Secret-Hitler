package com.example.secrethitler.ui.game

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.secrethitler.data.Player
import java.util.Stack

class GameViewModel : ViewModel() {

    var fascismCount = 0
    var liberalsCount = 0
    var hitlerCount = 1
    var communismCount = 0
    var stalinCount = 0

    private val laws by lazy { Stack<GameFragment.LAW>() }
    private val trashLaws = arrayListOf<GameFragment.LAW>()
    private val liberalSubmittedLaw = arrayListOf<GameFragment.LAW>()
    private val fascismSubmittedLaw = arrayListOf<GameFragment.LAW>()
    var currentPlayerIndex = 0
    val gamePlayers = arrayListOf<Player>()


    fun initLaws() {
        repeat(6) {
            trashLaws.add(GameFragment.LAW.LIBERAL)
        }
        repeat(11) {
            trashLaws.add(GameFragment.LAW.FASCISM)
        }
        trashLaws.shuffle()
        laws.addAll(trashLaws)
        trashLaws.clear()
        Log.i("SEPI", "initLaws: $laws")
    }

    fun submitLaw(last: GameFragment.LAW) {
        if (last.name == GameFragment.LAW.LIBERAL.name) {
            liberalSubmittedLaw.add(last)
            Log.i("SEPI", "submitLaw: liberal")
        } else {
            fascismSubmittedLaw.add(last)
            Log.i("SEPI", "submitLaw: fascism")
        }
    }

    fun getLaw(): GameFragment.LAW {
        if (laws.empty()) {
            trashLaws.shuffle()
            laws.addAll(trashLaws)
            Log.i("SEPI", "getLaw: shuffled new ${laws.size} ")
            Log.i("SEPI", "getLaw: submitted liberal ${liberalSubmittedLaw.size}")
            Log.i("SEPI", "getLaw: submitted fascism ${fascismSubmittedLaw.size}")
            trashLaws.clear()
        }
        return laws.pop()
    }

    fun getLawValue(textView: TextView): GameFragment.LAW {
        return if (textView.text.toString() == "Liberal") {
            GameFragment.LAW.LIBERAL
        } else {
            GameFragment.LAW.FASCISM
        }
    }

    fun trashTheLaw(textView: TextView) {
        trashLaws.add(getLawValue(textView))
    }

    fun getPlayerRoleText(): String = gamePlayers[currentPlayerIndex].role.toString()
    fun getPlayerRoleColor(): Int = gamePlayers[currentPlayerIndex].role.color


}