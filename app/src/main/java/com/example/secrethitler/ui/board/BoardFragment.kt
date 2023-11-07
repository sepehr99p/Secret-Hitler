package com.example.secrethitler.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.secrethitler.R
import com.example.secrethitler.data.Player
import com.example.secrethitler.databinding.FragmentBoardBinding
import com.example.secrethitler.databinding.WatchRoleBottomSheetBinding
import com.example.secrethitler.ui.board.watch_role.PlayerRoleAdapter
import com.example.secrethitler.ui.board.watch_role.PresidentRoleWatchListener
import com.example.secrethitler.ui.game.GameViewModel
import com.example.secrethitler.ui.utils.ViewHelper.hide
import com.example.secrethitler.ui.utils.ViewHelper.show
import com.google.android.material.bottomsheet.BottomSheetDialog

class BoardFragment constructor(
    private val viewModel: GameViewModel
) : Fragment() {


    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private val playerRoleAdapter by lazy { PlayerRoleAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(layoutInflater, container, false)
        initListeners()
        return binding.root
    }

    private fun initListeners() {
        with(binding) {
            watchRoleBtn.setOnClickListener {
                viewModel.presidentWatchCount--
                presentWatchRoleBottomSheet()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            fascismCount.text = viewModel.fascismSubmittedLaw.size.toString()
            liberalCount.text = viewModel.liberalSubmittedLaw.size.toString()
            communismCount.text = viewModel.communismSubmittedLaw.size.toString()
        }
        if (viewModel.presidentWatchCount > 0) {
            binding.watchRoleBtn.show()
        }
    }


    private fun presentWatchRoleBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        with(WatchRoleBottomSheetBinding.inflate(layoutInflater)) {
            bottomSheet.setContentView(root)
            bottomSheet.behavior.isDraggable = true
            bottomSheet.behavior.isFitToContents = true
            playerRoleAdapter.submitList(viewModel.gamePlayers)
            recyclerView.adapter = playerRoleAdapter
            playerRoleAdapter.presidentRoleWatchListener = object : PresidentRoleWatchListener {
                override fun onWatched(item: Player) {
                    viewModel.gamePlayers.remove(item)
                    finishWatchRole(bottomSheet)
                }
            }
            bottomSheet.setOnDismissListener {
                finishWatchRole(bottomSheet)
            }
        }
        bottomSheet.show()
    }

    private fun finishWatchRole(bottomSheet: BottomSheetDialog) {
        binding.watchRoleBtn.show()
        bottomSheet.dismiss()
        if (viewModel.presidentWatchCount == 0) {
            binding.watchRoleBtn.hide()
        }
    }


}