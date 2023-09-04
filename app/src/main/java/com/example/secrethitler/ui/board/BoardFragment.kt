package com.example.secrethitler.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.secrethitler.databinding.FragmentBoardBinding
import com.example.secrethitler.databinding.FragmentGameBinding
import com.example.secrethitler.databinding.FragmentMainTabBinding
import com.example.secrethitler.ui.game.GameViewModel

class BoardFragment constructor(
    private val viewModel: GameViewModel
) : Fragment() {


    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            fascismCount.text = viewModel.fascismSubmittedLaw.size.toString()
            liberalCount.text = viewModel.liberalSubmittedLaw.size.toString()
        }
    }

}