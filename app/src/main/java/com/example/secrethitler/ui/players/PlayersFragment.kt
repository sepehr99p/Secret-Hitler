package com.example.secrethitler.ui.players

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.google.android.gms.common.util.AndroidUtilsLight
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.security.AccessController


class PlayersFragment : Fragment() {

    private var _binding: FragmentPlayersBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { PlayersAdapter() }
    private val players = arrayListOf<String>()
    private val viewModel: PlayersViewModel by lazy {
        ViewModelProvider(
            this,
            PlayersViewModelFactory(
                PlayersPreferencesRepository(requireContext().playersPreferencesStore)
            )
        )[PlayersViewModel::class.java]
    }


    private val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
        ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
//            Toast.makeText(context, "on Move", Toast.LENGTH_SHORT).show()
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
//            Toast.makeText(context, "on Swiped ", Toast.LENGTH_SHORT).show()
            //Remove swiped item from list and notify the RecyclerView
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
                players.add(adapterPosition,addPlayerNameEt.text.toString())
//                adapter.submitList(players)

                adapter.notifyItemChanged(adapterPosition)
                bottomSheet.dismiss()
            }
            addPlayerNameEt.setText(adapter.currentList.get(adapterPosition))
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
            presentAddPlayerBottomSheet()
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