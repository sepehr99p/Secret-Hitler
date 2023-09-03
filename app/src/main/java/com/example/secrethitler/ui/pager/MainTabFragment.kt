package com.example.secrethitler.ui.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.secrethitler.databinding.FragmentMainTabBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainTabFragment : Fragment() {

    private var _binding: FragmentMainTabBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { GamePagerAdapter(lifecycle,childFragmentManager) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainTabBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int -> tab.text = "Tab " + (position + 1) }.attach()

        return binding.root
    }



}