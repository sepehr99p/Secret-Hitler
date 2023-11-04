package com.example.secrethitler.ui.board.watch_role

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.ui.utils.ViewHelper.show


class PlayerRoleAdapter : ListAdapter<Player, PlayerRoleViewHolder>(PLAYERS_COMPARATOR) {

    private var isVisible = false
    var presidentRoleWatchListener: PresidentRoleWatchListener? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private val PLAYERS_COMPARATOR = object : DiffUtil.ItemCallback<Player>() {
            override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerRoleViewHolder {
        val view = PlayerRoleViewHolder.create(parent)
        view.itemView.setOnClickListener {
            if (isVisible) {
                return@setOnClickListener
            } else {
                showRole(view)
                startTimer(view.adapterPosition)
            }
        }
        return view
    }

    override fun onBindViewHolder(holder: PlayerRoleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun showRole(holder: PlayerRoleViewHolder) {
        checkExceptions(holder)
        holder.binding.playerRoleTv.show()
        isVisible = true
    }

    private fun checkExceptions(holder: PlayerRoleViewHolder) {
        checkHitlerException(holder)
    }

    private fun checkHitlerException(holder: PlayerRoleViewHolder) {
        if (holder.binding.playerRoleTv.text.contains(ROLE.HITLER.name)) {
            holder.binding.playerRoleTv.text = ROLE.FASCISM.name
        }
    }

    private fun startTimer(adapterPosition: Int) {
        handler.postDelayed(timerRunnable(adapterPosition), 5000)
    }

    private fun timerRunnable(adapterPosition: Int) = Runnable {
        isVisible = false
        presidentRoleWatchListener?.onWatched(getItem(adapterPosition))
//            notifyDataSetChanged()
        notifyItemRemoved(adapterPosition)
    }


}