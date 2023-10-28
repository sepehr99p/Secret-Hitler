package com.example.secrethitler.ui.board.watch_role

import android.os.Handler
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.ui.utils.ViewHelper.show


class PlayerRoleAdapter : ListAdapter<Player, PlayerRoleViewHolder>(PLAYERS_COMPARATOR) {

    var isVisible = false
    var presidentRoleWatchListener : PresidentRoleWatchListener? = null

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
                startTimer()
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
        checkMohammadException(holder)
    }

    private fun checkHitlerException(holder: PlayerRoleViewHolder) {
        if (holder.binding.playerRoleTv.text.contains(ROLE.HITLER.name)) {
            holder.binding.playerRoleTv.text = ROLE.FASCISM.name
        }
    }

    private fun checkMohammadException(holder: PlayerRoleViewHolder) {
        if (holder.binding.playerNameTv.text == "mohammad" || holder.binding.playerNameTv.text == "Mohammad" || holder.binding.playerNameTv.text == "emam") {
            holder.binding.playerRoleTv.text = "آناناس"
        }
    }

    private fun startTimer() {
        Handler().postDelayed(5000) {
            isVisible = false
            presidentRoleWatchListener?.onWatched()
            notifyDataSetChanged()
        }
    }


}