package com.example.kingoftokyo.ui.game

import DiceAdapter
import PlayerCharacter
import PlayerModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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

class GameFragment : Fragment(), DiceAdapter.DiceClickListener {
    private lateinit var viewModel: GameViewModel
    private lateinit var player: PlayerModel
    private lateinit var opponentAdapter: OpponentAdapter
    private lateinit var diceAdapter: DiceAdapter
    private lateinit var diceList: List<DiceModel>
    private lateinit var king: PlayerModel

    private var remainingRollsValue: Int = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val selectedCharacter = arguments?.getParcelable<PlayerCharacter>("selectedCharacter")
        val playerName = arguments?.getString("playerName")

        player = PlayerModel( selectedCharacter!!.id, playerName!!, selectedCharacter.characterImageResId, 0, 10, 0)

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
        val playerAvatar = view.findViewById<ImageView>(R.id.playerAvatar)

        val kingAvatar = view.findViewById<ImageView>(R.id.boardgameKing)
        kingAvatar.setImageResource(viewModel.currentKing.value?.characterImageResId ?: player.characterImageResId)

        playerUsernameText.text = player.name
        playerAvatar.setImageResource(player.characterImageResId)

        val rollButton = view.findViewById<Button>(R.id.boardGameRollButton)

        rollButton.setOnClickListener {
            openCustomModal()
        }

        viewModel.currentState.observe(viewLifecycleOwner) { gamestate ->
            rollButton.isEnabled = gamestate == GameState.RollDiceState
            // TODO: activer le bouton d'inventaire uniquement quand c'est la phase d'achat
            when (gamestate) {
                GameState.RollDiceState -> {
                    remainingRollsValue = 3
                    diceList = listOf(
                        DiceModel(0, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(1, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(2, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(3, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(4, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(5, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true)
                    )
                }
                GameState.ResolveDiceState -> {
                    val diceResults = viewModel.calculateDiceResults(diceList)
                    Log.d("results", diceResults.toString())
                    opponentAdapter.updateOpponents(diceResults!!)
                    playerHPText.text = viewModel.player.value?.healthPoints.toString()
                    playerVPText.text = viewModel.player.value?.victoryPoints.toString()
                    viewModel.endTurn()
                }
                GameState.BuyState -> {}
                GameState.AttackState -> {}
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

        diceAdapter = DiceAdapter(diceList, this)
        diceRecyclerView.adapter = diceAdapter
        diceRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)
        val rollButton = dialogView.findViewById<Button>(R.id.rollDiceButton)
        val remainingRolls = dialogView.findViewById<TextView>(R.id.reamainingRollsValue)
        remainingRolls.text = remainingRollsValue.toString()
        val alertDialog = dialogBuilder.create()

        closeButton.setOnClickListener {
            viewModel.endTurn()
            alertDialog.dismiss()
        }

        rollButton.setOnClickListener {
            val diceResults = viewModel.rollDices(diceAdapter.diceList)
            remainingRollsValue--
            remainingRolls.text = remainingRollsValue.toString()
            if (remainingRollsValue == 0) {
                rollButton.isEnabled = false
            }
            diceList = diceResults
            diceAdapter.updateDice(diceResults)
        }

        alertDialog.show()
    }

    override fun onDiceClicked(diceId: Int) {
        diceAdapter.toggleRollability(diceId)
    }
}