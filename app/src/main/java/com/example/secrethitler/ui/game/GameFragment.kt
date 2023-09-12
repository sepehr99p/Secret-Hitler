package com.example.secrethitler.ui.game

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.secrethitler.R
import com.example.secrethitler.data.Player
import com.example.secrethitler.data.ROLE
import com.example.secrethitler.databinding.FragmentGameBinding
import com.example.secrethitler.ui.utils.ViewHelper.hide
import com.example.secrethitler.ui.utils.ViewHelper.invisible
import com.example.secrethitler.ui.utils.ViewHelper.show
import kotlin.random.Random

class GameFragment constructor(
    private val viewModel: GameViewModel
) : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val players = mutableListOf<String>()
    private val rolesInitList = mutableListOf<ROLE>()

    enum class LAW {
        LIBERAL,
        FASCISM
    }

    private val binding get() = _binding!!


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
        viewModel.whoWon.observe(viewLifecycleOwner) {
            var icon : Int = R.drawable.liberal_article
            if (it == ROLE.FASCISM) {
                icon = R.drawable.fascist_article
            } else if (it == ROLE.LIBERAL) {
                icon = R.drawable.liberal_article
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Game over")
                .setMessage("${it.name} WON !!!")
                .setCancelable(false)
                .setIcon(icon)
                .create().show()
        }
    }


    private fun initView() {
        with(binding) {
            playerRoleTextView.hide()
            playerNameTextView.text = viewModel.gamePlayers[viewModel.currentPlayerIndex].name
            watchLawBtn.hide()
        }
    }

    private fun initRoles(
        communismCount: Int,
        stalinCount: Int,
        fascismCount: Int,
        liberalCount: Int
    ) {
        createRole(communismCount, ROLE.COMMUNISM)
        createRole(stalinCount, ROLE.STALIN)
        createRole(fascismCount, ROLE.FASCISM)
        createRole(liberalCount, ROLE.LIBERAL)
        createRole(1, ROLE.HITLER)
        rolesInitList.shuffle(Random(System.currentTimeMillis()))
        players.shuffle(Random(System.currentTimeMillis() / 3))
        initFinalPlayerList()
    }

    private fun initFinalPlayerList() {
        for (i in 0 until players.size) {
            viewModel.gamePlayers.add(Player(players.removeLast(), rolesInitList.removeLast()))
        }
    }

    private fun createRole(count: Int, role: ROLE) {
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
                    initRoles(communismCount, stalinCount, fascismCount, liberalsCount)
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
            initRoles(communismCount, stalinCount, fascismCount, liberalsCount)
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
            watchLawBtn.setOnClickListener {
                showLaw()
            }
            law1Iv.setOnClickListener {
                checkFinishedCabine(law1Iv)
            }
            law2Iv.setOnClickListener {
                checkFinishedCabine(law2Iv)
            }
            law3Iv.setOnClickListener {
                checkFinishedCabine(law3Iv)
            }
        }
    }

    private fun getLawValue(imageView: ImageView): LAW {
        return if ((imageView.drawable as BitmapDrawable).bitmap == (ContextCompat.getDrawable(
                requireContext(),
                R.drawable.liberal_article
            ) as BitmapDrawable).bitmap
        ) {
            LAW.LIBERAL
        } else {
            LAW.FASCISM
        }
    }

    private fun trashTheLaw(imageView: ImageView) {
        viewModel.trashLaws.add(getLawValue(imageView))
    }

    private fun checkFinishedCabine(lawIv: ImageView) {
        var count = 0
        lateinit var law: LAW
        if (binding.law1Iv.isVisible) {
            count++
            law = getLawValue(binding.law1Iv)
        }
        if (binding.law2Iv.isVisible) {
            count++
            law = getLawValue(binding.law2Iv)
        }
        if (binding.law3Iv.isVisible) {
            count++
            law = getLawValue(binding.law3Iv)
        }
        if (count == 1) {
            viewModel.submitLaw(law)
            binding.watchLawBtn.show()
            binding.lawMainLl.hide()
        } else if (count > 1) {
            trashTheLaw(lawIv)
        }
        lawIv.invisible()
    }

    private fun presentInGameScreen() {
        binding.playerRoleTextView.hide()
        binding.playerNameTextView.hide()
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
            setLawCard(law1Iv)
            setLawCard(law2Iv)
            setLawCard(law3Iv)
            lawMainLl.show()
            law1Iv.show()
            law2Iv.show()
            law3Iv.show()
            watchLawBtn.hide()
        }
    }

    private fun setLawCard(lawIv: ImageView) {
        if (viewModel.getLaw() == LAW.LIBERAL) {
            lawIv.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.liberal_article
                )
            )
        } else {
            lawIv.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.fascist_article
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
