package com.example.kingoftokyo.ui.game

import PlayerCharacter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.ui.game.adapter.OpponentAdapter

class GameFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
    private lateinit var selectedCharacter: PlayerCharacter
    private lateinit var opponentAdapter: OpponentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCharacter = PlayerCharacter(1, "Croco Feroce", R.drawable.croco)

        viewModel = GameViewModel(view)
        viewModel.initCharactersList(selectedCharacter.id)
        opponentAdapter = OpponentAdapter(viewModel.opponents.value!!)

        val opponentRecyclerView = view.findViewById<RecyclerView>(R.id.gameboardOpponentCards)
        opponentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        opponentRecyclerView.adapter = opponentAdapter

        val playerHPText = view.findViewById<TextView>(R.id.gameboardPlayerHP)
        val playerVPText = view.findViewById<TextView>(R.id.gameboardPlayerVP)
        val playerUsernameText = view.findViewById<TextView>(R.id.gameboardUsername)

        playerUsernameText.text = selectedCharacter.name
    }
}