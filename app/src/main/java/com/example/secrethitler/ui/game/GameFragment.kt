package com.example.secrethitler.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.secrethitler.ui.MainActivity
import com.example.secrethitler.R
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.databinding.FragmentGameBinding
import kotlin.random.Random

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val players by lazy { (activity as MainActivity).players }
    private var currentPlayerIndex = 0
    private var fascism = 0
    private var liberals = 0
    private var hitler = 1
    private val gamePlayers = arrayListOf<Player>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayersRoles()
    }

    private fun initPlayersRoles() {
        when(players.size) {
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
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.text = players[currentPlayerIndex]
        initRoles()
    }

    private fun initRoles() {
        for (i in 0 until players.size) {
            val random = Random(System.currentTimeMillis())
            val nextRole = random.nextInt(hitler + liberals + fascism)
            if (nextRole == hitler && (hitler > 0)) {
                hitler = 0
                gamePlayers.add(Player(players[i],ROLE.HITLER))
            } else if (nextRole <= (hitler+fascism) && (fascism > 0)) {
                fascism--
                gamePlayers.add(Player(players[i],ROLE.FASCISM))
            } else {
                liberals--
                gamePlayers.add(Player(players[i],ROLE.LIBERAL))
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            root.setOnClickListener {
                if (currentPlayerIndex==players.size) {
                    clearScreen()
                } else {
                    if (playerRoleTextView.isVisible) {
                        currentPlayerIndex++
                        if (currentPlayerIndex==players.size) {
                            clearScreen()
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

    private fun clearScreen() {
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.hide()
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


    private fun View.hide() {
        this.visibility = View.GONE
    }

    private fun View.show() {
        this.visibility = View.VISIBLE
    }

}