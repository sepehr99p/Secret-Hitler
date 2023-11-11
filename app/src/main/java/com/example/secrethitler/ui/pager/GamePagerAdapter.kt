package com.example.secrethitler.ui.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.secrethitler.ui.MainActivity
import com.example.secrethitler.ui.board.BoardFragment
import com.example.secrethitler.ui.game.GameFragment
import com.example.secrethitler.ui.game.GameViewModel

class GamePagerAdapter(
    lifecycle: Lifecycle,
    fragmentManager: FragmentManager
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            GameFragment()
        } else {
            BoardFragment()
        }
    }


}