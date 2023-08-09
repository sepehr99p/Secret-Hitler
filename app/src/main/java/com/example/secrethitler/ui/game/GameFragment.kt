package com.example.secrethitler.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.secrethitler.ui.MainActivity
import com.example.secrethitler.R
import com.example.secrethitler.databinding.FragmentGameBinding
import kotlin.random.Random

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val players by lazy { (activity as MainActivity).players }
    private var currentPlayerIndex = 0
    private var fascism = 0
    private var liberals = 0
    private var hitler = 1

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
    }

    private fun initListeners() {
        with(binding) {
            root.setOnClickListener {
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

    private fun clearScreen() {
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.hide()
    }

    private fun showRole() {
        with(binding) {
            playerRoleTextView.show()
            // todo : set the role textView here later
            val random = Random(System.currentTimeMillis())
            var nextRole = random.nextInt(hitler + liberals + fascism)
            if (nextRole == hitler && (hitler > 0)) {
                hitler = 0
                playerRoleTextView.text = "Hitler"
                playerRoleTextView.setTextColor(requireContext().resources.getColor(R.color.red))
            } else if (nextRole <= (hitler+fascism) && (fascism > 0)) {
                fascism--
                playerRoleTextView.text = "Fascism"
                playerRoleTextView.setTextColor(requireContext().resources.getColor(R.color.red))
            } else {
                liberals--
                playerRoleTextView.text = "Liberal"
                playerRoleTextView.setTextColor(requireContext().resources.getColor(R.color.blue))
            }
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