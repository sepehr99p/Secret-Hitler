package com.example.secrethitler.ui.players

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.secrethitler.R
import com.example.secrethitler.databinding.PlayerItemBinding

class PlayerViewHolder(
    private val binding: PlayerItemBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(name: String) {
        binding.nameTv.text = name
    }

    companion object {
        fun create(parent: ViewGroup): PlayerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_item, parent, false)
            val binding = PlayerItemBinding.bind(view)
            return PlayerViewHolder(binding)
        }
    }

}