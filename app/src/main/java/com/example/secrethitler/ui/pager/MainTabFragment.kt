package com.example.secrethitler.ui.pager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.secrethitler.R
import com.example.secrethitler.data.PlayersPreferencesRepository
import com.example.secrethitler.databinding.FragmentMainTabBinding
import com.example.secrethitler.ui.MainActivity
import com.example.secrethitler.ui.game.GameViewModel
import com.example.secrethitler.ui.game.GameViewModelFactory
import com.example.secrethitler.ui.playersPreferencesStore
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainTabFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private var _binding: FragmentMainTabBinding? = null
    private val binding get() = _binding!!


    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            GameViewModelFactory(
                PlayersPreferencesRepository(requireContext().playersPreferencesStore)
            )
        )[GameViewModel::class.java]
    }

    private val adapter by lazy { GamePagerAdapter(lifecycle, childFragmentManager, viewModel) }

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
                        .setTitle("Quit")
                        .setMessage("Are you sure?")
                        .setNegativeButton("NO") { _, _ -> }
                        .setPositiveButton("YES") { _, _ ->
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
        Log.i("TAG", "onTabSelected: ")
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