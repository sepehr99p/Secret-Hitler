package com.example.secrethitler.ui.players

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secrethitler.R
import com.example.secrethitler.data.PlayersPreferencesRepository
import com.example.secrethitler.databinding.FragmentPlayersBinding
import com.example.secrethitler.databinding.PlayerCreationBottomSheetBinding
import com.example.secrethitler.ui.playersPreferencesStore
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections


@AndroidEntryPoint
class PlayersFragment : Fragment() {

    private var _binding: FragmentPlayersBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { PlayersAdapter() }
    private val players = arrayListOf<String>()
    private val viewModel: PlayersViewModel by viewModels()

    private val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
        ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    or ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    or ItemTouchHelper.DOWN or ItemTouchHelper.UP
        ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(players, fromPosition, toPosition)
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }


        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags =
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            val swipeFlags =
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            if (swipeDir == ItemTouchHelper.LEFT) {
                val position = viewHolder.adapterPosition
                players.removeAt(position)
                adapter.submitList(players)
                adapter.notifyItemRemoved(position)
            }
            if (swipeDir == ItemTouchHelper.RIGHT) {
                editCurrentPlayer(viewHolder.adapterPosition)
            }
        }

//        override fun onChildDraw(
//            c: Canvas,
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder,
//            dX: Float,
//            dY: Float,
//            actionState: Int,
//            isCurrentlyActive: Boolean
//        ) {
//            if (actionState === ItemTouchHelper.ACTION_STATE_SWIPE) {
//                val itemView = viewHolder.itemView
//                val paint = Paint()
//                val bitmap: Bitmap
//
//                if (dX > 0) { // swiping right
//                    paint.color = resources.getColor(R.color.blue)
//                    c.drawRect(
//                        itemView.left.toFloat() ,
//                        itemView.top.toFloat() + 16,
//                        dX,
//                        itemView.bottom.toFloat(),
//                        paint
//                    )
//                } else { // swiping left
//                    paint.color = resources.getColor(R.color.red)
//                    c.drawRect(
//                        itemView.right.toFloat() + dX,
//                        itemView.top.toFloat(),
//                        itemView.right.toFloat() + 16,
//                        itemView.bottom.toFloat(),
//                        paint
//                    )
//                }
//                super.onChildDraw(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    dX,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                )
//            }
//        }
    }

    private fun editCurrentPlayer(adapterPosition: Int) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding = PlayerCreationBottomSheetBinding.inflate(layoutInflater)
        with(bottomSheetBinding) {
            bottomSheet.setContentView(root)
            bottomSheet.behavior.isDraggable = true
            bottomSheet.behavior.isFitToContents = true
            addPlayerSubmitBtn.setOnClickListener {
                players.removeAt(adapterPosition)
                players.add(adapterPosition, addPlayerNameEt.text.toString())
//                adapter.submitList(players)
                adapter.notifyItemChanged(adapterPosition)
                bottomSheet.dismiss()
            }
            bottomSheet.setOnDismissListener {
                players.removeAt(adapterPosition)
                players.add(adapterPosition, addPlayerNameEt.text.toString())
                adapter.notifyItemChanged(adapterPosition)
            }
            addPlayerNameEt.setText(adapter.currentList[adapterPosition])
            addPlayerNameEt.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    players.add(addPlayerNameEt.text.toString())
                    adapter.submitList(players)
                    adapter.notifyItemInserted(adapter.itemCount + 1)
                    bottomSheet.dismiss()
                    handled = true
                }
                handled
            }
        }
        bottomSheet.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initListeners()
        initObservers()
        binding.versionTv.text = requireContext().packageManager.getPackageInfo(
            requireContext().packageName,
            0
        ).versionName
        binding.playBtn.setOnClickListener {
            if ((players.size >= 5) && (players.size <= 12)) {
                viewModel.updatePlayersList(players)
                findNavController().navigate(R.id.action_FirstFragment_to_MainTabFragment)
            } else {
                Toast.makeText(
                    context,
                    requireContext().resources.getString(R.string.invalid_player_count),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initObservers() {
        viewModel.initialSetupEvent.observe(viewLifecycleOwner) {
            players.clear()
            players.addAll(it.namesList)
            adapter.submitList(players)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initListeners() {
        binding.fab.setOnClickListener {
            if (players.size < 12) {
                presentAddPlayerBottomSheet()
            } else {
                Toast.makeText(
                    requireContext(),
                    "can't add more than 12 players",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initRecyclerView() {
        binding.playersRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.playersRv.adapter = adapter
        adapter.submitList(players)
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.playersRv)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun presentAddPlayerBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bottomSheetBinding = PlayerCreationBottomSheetBinding.inflate(layoutInflater)
        with(bottomSheetBinding) {
            bottomSheet.setContentView(root)
            bottomSheet.behavior.isDraggable = true
            bottomSheet.behavior.isFitToContents = true
            addPlayerSubmitBtn.setOnClickListener {
                players.add(addPlayerNameEt.text.toString())
                adapter.submitList(players)
                adapter.notifyItemInserted(adapter.itemCount + 1)
                bottomSheet.dismiss()
            }
            addPlayerNameEt.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    players.add(addPlayerNameEt.text.toString())
                    adapter.submitList(players)
                    adapter.notifyItemInserted(adapter.itemCount + 1)
                    bottomSheet.dismiss()
                    handled = true
                }
                handled
            }
        }
        bottomSheet.show()
    }


}