package com.example.secrethitler.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private val playerRoleAdapter by lazy { PlayerRoleAdapter() }
    private val viewModel: GameViewModel by viewModels()

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
                if (viewModel.presidentWatchCount > 0) {
                    presentWatchRoleBottomSheet()
                }
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
        viewModel.presidentWatchCount--
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