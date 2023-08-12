package com.example.secrethitler.ui.game

import android.os.Handler
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.secrethitler.data.Player
import com.example.secrethitler.ui.utils.ViewHelper.show


class PlayerRoleAdapter : ListAdapter<Player, PlayerRoleViewHolder>(PLAYERS_COMPARATOR) {

    var isVisible = false

    companion object {
        private val PLAYERS_COMPARATOR = object : DiffUtil.ItemCallback<Player>() {
            override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerRoleViewHolder {
        return PlayerRoleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PlayerRoleViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            if (isVisible) {
                return@setOnClickListener
            } else {
                holder.binding.playerRoleTv.show()
                isVisible = true
                Handler().postDelayed(5000) {
                    isVisible = false
                    notifyDataSetChanged()
                }
            }
        }
    }


}