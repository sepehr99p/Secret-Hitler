package com.example.secrethitler.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.secrethitler.R
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.databinding.FragmentGameBinding
import com.example.secrethitler.ui.MainActivity
import com.example.secrethitler.ui.utils.ViewHelper.hide
import com.example.secrethitler.ui.utils.ViewHelper.invisible
import com.example.secrethitler.ui.utils.ViewHelper.show
import java.util.Stack
import kotlin.random.Random

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val players by lazy { (activity as MainActivity).players }
    private var currentPlayerIndex = 0
    private val gamePlayers = arrayListOf<Player>()
    private val playerRoleAdapter by lazy { PlayerRoleAdapter() }
    private val laws by lazy { Stack<LAW>() }
    private val trashLaws = arrayListOf<LAW>()
    private val liberalSubmitedLaw = arrayListOf<LAW>()
    private val fascismSubmitedLaw = arrayListOf<LAW>()
    enum class LAW(value : Int){
        LIBERAL(0),
        FASCISM(1)
    }

    private var fascismCount = 0
    private var liberalsCount = 0
    private var hitlerCount = 1
    private var communismCount = 0
    private var stalinCount = 0
    private val binding get() = _binding!!
    private var presidentWatchCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayersConfig()
    }

    private fun initPlayersConfig() {
        when (players.size) {
            12 -> {
                presidentWatchCount = 2
                communismCount = 1
                stalinCount = 1
                fascismCount = 3
                liberalsCount = players.size - hitlerCount - fascismCount
            }

            11 -> {
                presidentWatchCount = 2
                stalinCount = 1
                fascismCount = 3
                liberalsCount = players.size - hitlerCount - fascismCount
            }

            10, 9 -> {
                presidentWatchCount = 2
                fascismCount = 3
                liberalsCount = players.size - hitlerCount - fascismCount
            }

            8, 7 -> {
                fascismCount = 2
                liberalsCount = players.size - hitlerCount - fascismCount
            }

            6, 5 -> {
                presidentWatchCount = 0
                fascismCount = 1
                liberalsCount = players.size - hitlerCount - fascismCount
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        with(binding) {
            playerRoleTextView.hide()
            playerNameTextView.text = players[currentPlayerIndex]
            playersRecyclerView.adapter = playerRoleAdapter
            playerRoleAdapter.presidentRoleWatchListener = object : PresidentRoleWatchListener{
                override fun onWatched() {
                    hideListPlayers()
                }
            }
            playersRecyclerView.hide()
            watchRoleBtn.hide()
            watchLawBtn.hide()
        }
        initRoles()
        initLaws()
    }

    private fun initLaws() {
        repeat(6){
            trashLaws.add(LAW.LIBERAL)
        }
        repeat(11){
            trashLaws.add(LAW.FASCISM)
        }
        trashLaws.shuffle()
        laws.addAll(trashLaws)
        trashLaws.clear()
        Log.i("SEPI", "initLaws: $laws")
    }


    private fun initRoles() {
        for (i in 0 until players.size) {
            val random = Random(System.currentTimeMillis())
            val nextRole =
                random.nextInt(hitlerCount + liberalsCount + fascismCount + stalinCount + communismCount)
            if (nextRole == hitlerCount && (hitlerCount > 0)) {
                hitlerCount = 0
                gamePlayers.add(Player(players[i], ROLE.HITLER))
            } else if (nextRole <= (hitlerCount + fascismCount) && (fascismCount > 0)) {
                fascismCount--
                gamePlayers.add(Player(players[i], ROLE.FASCISM))
            } else if (nextRole <= (hitlerCount + fascismCount + communismCount) && (communismCount > 0)) {
                communismCount--
                gamePlayers.add(Player(players[i], ROLE.COMMUNISM))
            } else if (nextRole <= ((hitlerCount + fascismCount + communismCount) + stalinCount) && (stalinCount > 0)) {
                stalinCount--
                gamePlayers.add(Player(players[i], ROLE.STALIN))
            } else {
                liberalsCount--
                gamePlayers.add(Player(players[i], ROLE.LIBERAL))
            }
        }
        playerRoleAdapter.submitList(gamePlayers)
    }

    private fun initListeners() {
        with(binding) {
            root.setOnClickListener {
                if (currentPlayerIndex == players.size) {
                    presentInGameScreen()
                } else {
                    if (playerRoleTextView.isVisible) {
                        currentPlayerIndex++
                        if (currentPlayerIndex == players.size) {
                            presentInGameScreen()
                        } else {
                            showNextPlayer()
                        }
                    } else {
                        showRole()
                    }
                }
            }
            watchRoleBtn.setOnClickListener {
                presidentWatchCount--
                presentList()
            }
            watchLawBtn.setOnClickListener{
                showLaw()
            }
            law1Tv.setOnClickListener {
                law1Tv.invisible()
                trashLaws.add(getLawValue(law1Tv))
                checkFinishedCabine()
            }
            law2Tv.setOnClickListener {
                law2Tv.invisible()
                trashLaws.add(getLawValue(law2Tv))
                checkFinishedCabine()
            }
            law3Tv.setOnClickListener {
                law3Tv.invisible()
                trashLaws.add(getLawValue(law3Tv))
                checkFinishedCabine()
            }
        }
    }

    private fun checkFinishedCabine() {
        var count = 0
        if (binding.law1Tv.isVisible) {
            count++
        }
        if (binding.law2Tv.isVisible) {
            count++
        }
        if (binding.law3Tv.isVisible) {
            count++
        }
        if (count == 1) {
            submitLaw(laws.removeLast())
        }
        if (count == 0) {
            Log.i("SEPI", "checkFinishedCabine: count = 0")
            binding.lawMainLl.hide()
        }
    }

    private fun submitLaw(last: LAW) {
        if (last.name == LAW.LIBERAL.name) {
            liberalSubmitedLaw.add(last)
        } else {
            fascismSubmitedLaw.add(last)
        }
    }


    private fun hideListPlayers() {
        if (presidentWatchCount > 0) {
            binding.watchRoleBtn.show()
            binding.lawMainLl.show()
        }
        binding.playersRecyclerView.hide()
    }

    private fun presentList() {
        binding.watchRoleBtn.hide()
        binding.lawMainLl.hide()
        binding.playersRecyclerView.show()
    }

    private fun presentInGameScreen() {
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.hide()
        if (presidentWatchCount > 0) {
            binding.watchRoleBtn.show()
        }
        binding.watchLawBtn.show()
    }

    private fun showRole() {
        with(binding) {
            playerRoleTextView.show()
            playerRoleTextView.text = gamePlayers[currentPlayerIndex].role.toString()
            playerRoleTextView.setTextColor(requireContext().resources.getColor(gamePlayers[currentPlayerIndex].role.color))
        }
    }

    private fun showNextPlayer() {
        with(binding) {
            playerRoleTextView.hide()
            playerNameTextView.text = players[currentPlayerIndex]
        }
    }

    private fun showLaw() {
        with(binding) {
            setLawCard(law1Tv)
            setLawCard(law2Tv)
            setLawCard(law3Tv)
            lawMainLl.show()
            law1Tv.show()
            law2Tv.show()
            law3Tv.show()
        }
    }

    private fun setLawCard(lawTv: TextView) {
        if (getLaw() == LAW.LIBERAL) {
            lawTv.background = this.resources.getDrawable(R.drawable.bg_rectangle_blue)
            lawTv.text = "Liberal"
        } else {
            lawTv.background = this.resources.getDrawable(R.drawable.bg_rectangle_red)
            lawTv.text = "fascism"
        }
    }

    private fun getLaw(): LAW {
        if (laws.empty()) {
            trashLaws.shuffle()
            laws.addAll(trashLaws)
            Log.i("SEPI", "getLaw: shuffled new ${laws.size} ")
            Log.i("SEPI", "getLaw: submitted liberal ${liberalSubmitedLaw.size}")
            Log.i("SEPI", "getLaw: submitted fascism ${fascismSubmitedLaw.size}")
            trashLaws.clear()
        }
        return laws.pop()
    }

    private fun getLawValue(textView: TextView): LAW {
        return if (textView.text.toString() == "Liberal") {
            LAW.LIBERAL
        } else {
            LAW.FASCISM
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}