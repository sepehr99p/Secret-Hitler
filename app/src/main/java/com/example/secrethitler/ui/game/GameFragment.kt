package com.example.secrethitler.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.secrethitler.ui.MainActivity
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.databinding.FragmentGameBinding
import com.example.secrethitler.ui.utils.ViewHelper.hide
import com.example.secrethitler.ui.utils.ViewHelper.show
import kotlin.random.Random

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val players by lazy { (activity as MainActivity).players }
    private var currentPlayerIndex = 0
    private var fascism = 0
    private var liberals = 0
    private var hitler = 1
    private var communism = 0
    private var stalin = 0
    private val gamePlayers = arrayListOf<Player>()
    private val playerRoleAdapter by lazy { PlayerRoleAdapter() }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayersConfig()
    }

    private fun initPlayersConfig() {
        when(players.size) {
            12 -> {
                communism = 1
                stalin = 1
                fascism = 3
                liberals = players.size - hitler - fascism
            }
            11 -> {
                stalin = 1
                fascism = 3
                liberals = players.size - hitler - fascism
            }
            10,9 -> {
                fascism = 3
                liberals = players.size - hitler - fascism
            }
            8,7 -> {
                fascism = 2
                liberals = players.size - hitler - fascism
            }
            6,5 -> {
                fascism = 1
                liberals = players.size - hitler - fascism
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
            playersRecyclerView.hide()
        }
        initRoles()
    }

    private fun initRoles() {
        for (i in 0 until players.size) {
            val random = Random(System.currentTimeMillis())
            val nextRole = random.nextInt(hitler + liberals + fascism + stalin + communism)
            if (nextRole == hitler && (hitler > 0)) {
                hitler = 0
                gamePlayers.add(Player(players[i],ROLE.HITLER))
            } else if (nextRole <= (hitler+fascism) && (fascism > 0)) {
                fascism--
                gamePlayers.add(Player(players[i],ROLE.FASCISM))
            } else if(nextRole <= (hitler+fascism+communism) && (communism > 0)) {
                communism--
                gamePlayers.add(Player(players[i],ROLE.COMMUNISM))
            } else if(nextRole <= ((hitler+fascism+communism)+stalin) && (stalin > 0)) {
                stalin--
                gamePlayers.add(Player(players[i],ROLE.STALIN))
            } else {
                liberals--
                gamePlayers.add(Player(players[i],ROLE.LIBERAL))
            }
        }
        playerRoleAdapter.submitList(gamePlayers)
    }

    private fun initListeners() {
        with(binding) {
            root.setOnClickListener {
                if (currentPlayerIndex==players.size) {
                    presentInGameScreen()
                } else {
                    if (playerRoleTextView.isVisible) {
                        currentPlayerIndex++
                        if (currentPlayerIndex==players.size) {
                            presentInGameScreen()
                        } else {
                            showNextPlayer()
                        }
                    } else {
                        showRole()
                    }
                }
            }
        }
    }

    private fun presentInGameScreen() {
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.hide()
        binding.playersRecyclerView.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}