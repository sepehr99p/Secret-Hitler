package com.example.secrethitler.ui.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.secrethitler.R
import com.example.secrethitler.databinding.FragmentMainTabBinding
import com.example.secrethitler.ui.MainActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainTabFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private var _binding: FragmentMainTabBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { GamePagerAdapter(lifecycle, childFragmentManager) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainTabBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = adapter


        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int -> tab.text = "Screen " + (position + 1) }.attach()


        binding.tabLayout.addOnTabSelectedListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.quit))
                        .setMessage(getString(R.string.are_you_sure))
                        .setNegativeButton(getString(R.string.no)) { _, _ -> }
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            val bundle = Bundle()
                            bundle.putBoolean("reset", true)
                            findNavController().navigate(
                                R.id.action_MainTabFragment_to_FirstFragment,
                                bundle
                            )
                        }
                        .create()
                        .show()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.tabLayout.removeOnTabSelectedListener(this)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let { _tab ->
            if (_tab.position != 0) {
                if (MainActivity.rolesAreWatched) {
                    binding.viewPager.currentItem = _tab.position
                }
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }


}