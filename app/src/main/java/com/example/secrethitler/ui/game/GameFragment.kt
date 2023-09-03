package com.example.secrethitler.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.secrethitler.R
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.PlayersPreferencesRepository
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.databinding.FragmentGameBinding
import com.example.secrethitler.ui.playersPreferencesStore
import com.example.secrethitler.ui.utils.ViewHelper.hide
import com.example.secrethitler.ui.utils.ViewHelper.invisible
import com.example.secrethitler.ui.utils.ViewHelper.show
import kotlin.random.Random

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val players = mutableListOf<String>()
    private val rolesInitList = mutableListOf<ROLE>()
    private val playerRoleAdapter by lazy { PlayerRoleAdapter() }
    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            GameViewModelFactory(
                PlayersPreferencesRepository(requireContext().playersPreferencesStore)
            )
        ).get(GameViewModel::class.java)
    }


    enum class LAW {
        LIBERAL,
        FASCISM
    }

    private val binding get() = _binding!!
    private var presidentWatchCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.initialSetupEvent.observe(viewLifecycleOwner) {
            players.clear()
            players.addAll(it.namesList)
            initPlayersConfig()
            viewModel.initLaws()
            initView()
        }
    }

    private fun initView() {
        with(binding) {
            playerRoleTextView.hide()
            playerNameTextView.text = viewModel.gamePlayers[viewModel.currentPlayerIndex].name
            playersRecyclerView.adapter = playerRoleAdapter
            playerRoleAdapter.presidentRoleWatchListener = object : PresidentRoleWatchListener {
                override fun onWatched() {
                    hideListPlayers()
                }
            }
            playersRecyclerView.hide()
            watchRoleBtn.hide()
            watchLawBtn.hide()
        }
    }

    private fun initRoles(communismCount : Int, stalinCount : Int, fascismCount : Int, liberalCount : Int) {
        createRole(communismCount,ROLE.COMMUNISM)
        createRole(stalinCount,ROLE.STALIN)
        createRole(fascismCount,ROLE.FASCISM)
        createRole(liberalCount,ROLE.LIBERAL)
        createRole(1,ROLE.HITLER)
        rolesInitList.shuffle(Random(System.currentTimeMillis()))
        players.shuffle(Random(System.currentTimeMillis()/3))
        initFinalPlayerList()
        playerRoleAdapter.submitList(viewModel.gamePlayers)
    }

    private fun initFinalPlayerList() {
        for (i in 0 until  players.size) {
            viewModel.gamePlayers.add(Player(players.removeLast(),rolesInitList.removeLast()))
        }
    }

    private fun createRole(count : Int, role : ROLE) {
        for (i in 0 until count) {
            rolesInitList.add(role)
        }
    }

    private fun initPlayersConfig() {
        with(viewModel) {
            when (players.size) {
                12 -> {
                    presidentWatchCount = 2
                    communismCount = 1
                    stalinCount = 1
                    fascismCount = 3
                    liberalsCount = players.size - hitlerCount - fascismCount
                    initRoles(communismCount,stalinCount,fascismCount,liberalsCount)
                }

                11 -> {
                    presidentWatchCount = 2
                    stalinCount = 1
                    fascismCount = 3
                    liberalsCount = players.size - hitlerCount - fascismCount
                }

                10, 9 -> {
                    presidentWatchCount = 2
                    fascismCount = 3
                    liberalsCount = players.size - hitlerCount - fascismCount
                }

                8, 7 -> {
                    presidentWatchCount = 1
                    fascismCount = 2
                    liberalsCount = players.size - hitlerCount - fascismCount
                }

                6, 5 -> {
                    presidentWatchCount = 0
                    fascismCount = 1
                    liberalsCount = players.size - hitlerCount - fascismCount
                }
            }
            initRoles(communismCount,stalinCount,fascismCount,liberalsCount)
        }
    }

    private fun initListeners() {
        with(binding) {
            root.setOnClickListener {
                if (viewModel.currentPlayerIndex == viewModel.gamePlayers.size) {
                    presentInGameScreen()
                } else {
                    if (playerRoleTextView.isVisible) {
                        viewModel.currentPlayerIndex++
                        if (viewModel.currentPlayerIndex == viewModel.gamePlayers.size) {
                            presentInGameScreen()
                        } else {
                            showNextPlayer()
                        }
                    } else {
                        showRole()
                    }
                }
            }
            watchRoleBtn.setOnClickListener {
                presidentWatchCount--
                presentList()
            }
            watchLawBtn.setOnClickListener {
                showLaw()
            }
            law1Tv.setOnClickListener {
                checkFinishedCabine(law1Tv)
            }
            law2Tv.setOnClickListener {
                checkFinishedCabine(law2Tv)
            }
            law3Tv.setOnClickListener {
                checkFinishedCabine(law3Tv)
            }
        }
    }

    private fun checkFinishedCabine(lawTv: TextView) {
        var count = 0
        lateinit var law: LAW
        if (binding.law1Tv.isVisible) {
            count++
            law = viewModel.getLawValue(binding.law1Tv)
        }
        if (binding.law2Tv.isVisible) {
            count++
            law = viewModel.getLawValue(binding.law2Tv)
        }
        if (binding.law3Tv.isVisible) {
            count++
            law = viewModel.getLawValue(binding.law3Tv)
        }
        if (count == 1) {
            viewModel.submitLaw(law)
        } else if (count > 1) {
            viewModel.trashTheLaw(lawTv)
        }
        if (count == 0) {
            Log.i("SEPI", "checkFinishedCabine: count = 0")
            binding.lawMainLl.hide()
        }
        lawTv.invisible()
    }


    private fun hideListPlayers() {
        if (presidentWatchCount > 0) {
            binding.watchRoleBtn.show()
            binding.lawMainLl.show()
        }
        binding.playersRecyclerView.hide()
    }

    private fun presentList() {
        binding.watchRoleBtn.hide()
        binding.lawMainLl.hide()
        binding.playersRecyclerView.show()
    }

    private fun presentInGameScreen() {
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.hide()
        if (presidentWatchCount > 0) {
            binding.watchRoleBtn.show()
        }
        binding.watchLawBtn.show()
    }

    private fun showRole() {
        with(binding) {
            playerRoleTextView.show()
            playerRoleTextView.text = viewModel.getPlayerRoleText()
            playerRoleTextView.setTextColor(requireContext().resources.getColor(viewModel.getPlayerRoleColor()))
        }
    }

    private fun showNextPlayer() {
        with(binding) {
            playerRoleTextView.hide()
            playerNameTextView.text = viewModel.gamePlayers[viewModel.currentPlayerIndex].name
        }
    }

    private fun showLaw() {
        with(binding) {
            setLawCard(law1Tv)
            setLawCard(law2Tv)
            setLawCard(law3Tv)
            lawMainLl.show()
            law1Tv.show()
            law2Tv.show()
            law3Tv.show()
        }
    }

    private fun setLawCard(lawTv: TextView) {
        if (viewModel.getLaw() == LAW.LIBERAL) {
            lawTv.background = this.resources.getDrawable(R.drawable.bg_rectangle_blue)
            lawTv.text = "Liberal"
        } else {
            lawTv.background = this.resources.getDrawable(R.drawable.bg_rectangle_red)
            lawTv.text = "fascism"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}