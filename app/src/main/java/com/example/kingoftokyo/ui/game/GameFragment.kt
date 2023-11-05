package com.example.kingoftokyo.ui.game

import CardAdapter
import DiceAdapter
import PlayerCharacter
import PlayerModel
import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.R.*
import com.example.kingoftokyo.model.Card
import com.example.kingoftokyo.model.DiceModel
import com.example.kingoftokyo.model.GameState
import com.example.kingoftokyo.ui.game.adapter.OpponentAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment(), DiceAdapter.DiceClickListener {
    private lateinit var viewModel: GameViewModel
    private lateinit var player: PlayerModel
    private lateinit var opponentAdapter: OpponentAdapter
    private lateinit var diceAdapter: DiceAdapter
    private lateinit var diceList: List<DiceModel>
    private lateinit var king: PlayerModel
    private var isAIPlaying: Boolean = false
    private var aiDiceRolled = false

    private var remainingRollsValue: Int = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val selectedCharacter = arguments?.getParcelable<PlayerCharacter>("selectedCharacter")
        val playerName = arguments?.getString("playerName")

        player = PlayerModel( selectedCharacter!!.id, playerName!!, selectedCharacter.characterImageResId, 0, 10, 0)

        return inflater.inflate(layout.fragment_game, container, false)
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
        val inventoryButton = view.findViewById<Button>(R.id.boardGameInventoryButton)

        rollButton.setOnClickListener {
            openCustomModal()
        }
        
        inventoryButton.setOnClickListener{
            openInventoryModal()
        }

        viewModel.currentState.observe(viewLifecycleOwner) { gamestate ->
            isAIPlaying = viewModel.currentPlayer.value?.id != viewModel.player.value?.id

            rollButton.isEnabled = (gamestate == GameState.RollDiceState) && !isAIPlaying
            Log.d("currentplayer", viewModel.currentPlayer.value?.name.toString())
            Log.d("gameState", gamestate.toString())
            Log.d("isRollEnabled", rollButton.isEnabled.toString())
            inventoryButton.isEnabled = (gamestate == GameState.BuyState) && !isAIPlaying

            viewModel.currentPlayer.value?.id?.let { opponentAdapter.updateCurrentPlayerId(it) }

            if (!isAIPlaying) {
                val borderDrawable = ContextCompat.getDrawable(view.context, R.drawable.border)
                val layers = arrayOf(playerAvatar.drawable, borderDrawable)
                val layerDrawable = LayerDrawable(layers)
                playerAvatar.setImageDrawable(layerDrawable)
            }
            when (gamestate) {
                GameState.RollDiceState -> {
                    aiDiceRolled = false
                    remainingRollsValue = 3
                    diceList = listOf(
                        DiceModel(0, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(1, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(2, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(3, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(4, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true),
                        DiceModel(5, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true)
                    )
                    if (isAIPlaying) {
                        openCustomModal()
                    }
                }
                GameState.ResolveDiceState -> {
                    val diceResults = viewModel.calculateDiceResults(diceList)
                    Log.d("results", diceResults.toString())
                    opponentAdapter.updateOpponents(diceResults!!)
                    playerHPText.text = viewModel.player.value?.healthPoints.toString()
                    playerVPText.text = viewModel.player.value?.victoryPoints.toString()
                    viewModel.goToNextState()
                }
                GameState.BuyState -> {
                    if (isAIPlaying) {
                        viewModel.goToNextState()
                    }
                }
                GameState.AttackState -> {
                    if (isAIPlaying) {
                        viewModel.goToNextState()
                    }
                }
                GameState.EndTurnState -> {
                   viewModel.endTurn()
                }
                null -> TODO()
            }
        }
    }

    fun openCustomModal() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(layout.dice_roll_modal_layout, null)
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

        if (!isAIPlaying) {
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
        } else if (!aiDiceRolled) {
            viewLifecycleOwner.lifecycleScope.launch  {
                aiDiceRolled = true
                delay(1500)
                alertDialog.show()
                rollButton.isEnabled = false
                closeButton.isEnabled = false

                var diceResults = viewModel.rollDices(diceAdapter.diceList)
                remainingRollsValue--
                remainingRolls.text = remainingRollsValue.toString()
                if (remainingRollsValue == 0) {
                    rollButton.isEnabled = false
                }
                diceList = diceResults
                diceAdapter.updateDice(diceResults)

                delay(2500)

                diceResults = viewModel.rollDices(diceAdapter.diceList)
                remainingRollsValue--
                remainingRolls.text = remainingRollsValue.toString()
                if (remainingRollsValue == 0) {
                    rollButton.isEnabled = false
                }
                diceList = diceResults
                diceAdapter.updateDice(diceResults)

                delay(2500)

                viewModel.goToNextState()
                alertDialog.dismiss()
            }
        }




    }

    private fun openInventoryModal() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val inventoryView = inflater.inflate(layout.inventory_modal, null)
        dialogBuilder.setView(inventoryView)

        val inventoryRecyclerView =
            inventoryView.findViewById<RecyclerView>(R.id.inventoryRecyclerView)
        val cardAdapter = CardAdapter(
            listOf(
                Card(1, "Card 1", drawable.crown, "Gagne 3 PV", 1, "type", "effect"),
                Card(2, "Card 2", drawable.crown, "Inflige 2 points de dégats supplémentaire a chaque joueur", 2, "type", "effect"),
                Card(3, "Card 3", drawable.crown, "Gagne une energie a chaque début de tour", 2, "type", "effect"),
                Card(4, "Card 4", drawable.crown, "Inflige les dégats recus au autres joueurs", 7, "type", "effect"),
                Card(5, "Card 5", drawable.crown, "Gagne 1 points de victoire a chaque début de tour", 4, "type", "effect"),
                Card(6, "Card 6", drawable.crown, "Gagne 2 points de victoire par attaque", 7, "type", "effect"),
                Card(7, "Card 7", drawable.crown, "Mange tes morts", 1, "type", "effect")
                                )
        )

        inventoryRecyclerView.adapter = cardAdapter
        inventoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val closeInventoryButton = inventoryView.findViewById<Button>(R.id.closeInventoryButton)

        val alertDialog = dialogBuilder.create()
        closeInventoryButton.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialog.show()
    }

    override fun onDiceClicked(diceId: Int) {
        diceAdapter.toggleRollability(diceId)
    }
}