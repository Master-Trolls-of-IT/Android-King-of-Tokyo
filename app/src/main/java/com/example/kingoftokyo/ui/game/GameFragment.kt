package com.example.kingoftokyo.ui.game

import PlayerCharacter
import PlayerModel
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
    private lateinit var player: PlayerModel
    private lateinit var opponentAdapter: OpponentAdapter
    private lateinit var king: PlayerModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val selectedCharacter = arguments?.getParcelable<PlayerCharacter>("selectedCharacter")
        val playerName = arguments?.getString("playerName")

        player = PlayerModel(playerName!!, selectedCharacter!!.id, selectedCharacter.characterImageResId, 20, 0)

        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = GameViewModel(view)
        viewModel.initCharactersList(player.id)
        opponentAdapter = OpponentAdapter(viewModel.opponents.value!!)

        val opponentRecyclerView = view.findViewById<RecyclerView>(R.id.gameboardOpponentCards)
        opponentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        opponentRecyclerView.adapter = opponentAdapter

        val playerHPText = view.findViewById<TextView>(R.id.gameboardPlayerHP)
        val playerVPText = view.findViewById<TextView>(R.id.gameboardPlayerVP)
        val playerUsernameText = view.findViewById<TextView>(R.id.gameboardUsername)

        playerUsernameText.text = player.name
    }
}