package com.example.kingoftokyo.ui.game

import DiceAdapter
import PlayerCharacter
import PlayerModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.DiceModel
import com.example.kingoftokyo.model.GameState
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

        viewModel = GameViewModel(view, player.id)
        opponentAdapter = OpponentAdapter(viewModel.opponents.value!!)

        val opponentRecyclerView = view.findViewById<RecyclerView>(R.id.gameboardOpponentCards)
        opponentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        opponentRecyclerView.adapter = opponentAdapter

        val playerHPText = view.findViewById<TextView>(R.id.gameboardPlayerHP)
        val playerVPText = view.findViewById<TextView>(R.id.gameboardPlayerVP)
        val playerUsernameText = view.findViewById<TextView>(R.id.gameboardUsername)

        playerUsernameText.text = player.name

        val rollButton = view.findViewById<Button>(R.id.boardGameRollButton)

        rollButton.setOnClickListener {
            openCustomModal()
        }

        viewModel.currentState.observe(viewLifecycleOwner) { gamestate ->
            when (gamestate) {
                GameState.RollDiceState -> {}
                GameState.BuyState -> {}
                GameState.AttackState -> {}
                GameState.ResolveDiceState -> {}
                GameState.EndTurnState -> {}
                null -> TODO()
            }
        }
    }

    fun openCustomModal() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dice_roll_modal_layout, null)
        dialogBuilder.setView(dialogView)

        val diceRecyclerView = dialogView.findViewById<RecyclerView>(R.id.diceRecyclerView)
        val diceAdapter = DiceAdapter(
            listOf(
                DiceModel("Croco Die", R.drawable.croco, "heal"),
                DiceModel("Croco Die", R.drawable.croco, "attack"),
                DiceModel("Croco Die", R.drawable.croco, "energy"),
                DiceModel("Croco Die", R.drawable.croco, "victory1"),
                DiceModel("Croco Die", R.drawable.croco, "victory2"),
                DiceModel("Croco Die", R.drawable.croco, "victory3")
            )
        )
        diceRecyclerView.adapter = diceAdapter
        diceRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)
        val alertDialog = dialogBuilder.create()

        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}