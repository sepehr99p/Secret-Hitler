package com.example.secrethitler.ui.pager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.secrethitler.ui.board.BoardFragment
import com.example.secrethitler.ui.game.GameFragment

class GamePagerAdapter constructor(
    private val lifecycle: Lifecycle,
    private val fragmentManager: FragmentManager
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            GameFragment()
        } else {
            BoardFragment()
        }
    }


}