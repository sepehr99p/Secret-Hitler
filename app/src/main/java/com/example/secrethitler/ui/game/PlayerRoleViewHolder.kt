package com.example.secrethitler.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.secrethitler.R
import com.example.secrethitler.data.Player
import com.example.secrethitler.databinding.PlayerRoleItemBinding
import com.example.secrethitler.ui.utils.ViewHelper.hide


class PlayerRoleViewHolder(
    val binding: PlayerRoleItemBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(player : Player) {
        binding.playerNameTv.text = player.name
        binding.playerRoleTv.text = player.role.toString()
        binding.playerRoleTv.hide()
    }

    companion object {
        fun create(parent: ViewGroup): PlayerRoleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.player_role_item, parent, false)
            val binding = PlayerRoleItemBinding.bind(view)
            return PlayerRoleViewHolder(binding)
        }
    }

}