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
import com.example.kingoftokyo.boilerplate.getGameState
import com.example.kingoftokyo.boilerplate.getInitialsCards
import com.example.kingoftokyo.boilerplate.getPredifinedCards
import com.example.kingoftokyo.model.Card
import com.example.kingoftokyo.model.DiceModel
import com.example.kingoftokyo.model.GameState
import com.example.kingoftokyo.ui.game.adapter.CardSlotAdpater
import com.example.kingoftokyo.ui.game.adapter.OpponentAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment(), DiceAdapter.DiceClickListener, CardSlotAdpater.CardSlotClickListener {
    private lateinit var viewModel: GameViewModel
    private lateinit var player: PlayerModel
    private lateinit var opponentAdapter: OpponentAdapter
    private lateinit var cardSlotAdapter: CardSlotAdpater
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

        player = PlayerModel( selectedCharacter!!.id, playerName!!, selectedCharacter.characterImageResId, 0, 10, 0, emptyList())

        return inflater.inflate(layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = GameViewModel(view, player.id)
        opponentAdapter = OpponentAdapter(viewModel.opponents.value!!)
        cardSlotAdapter = CardSlotAdpater(getInitialsCards(view), this)


        val opponentRecyclerView = view.findViewById<RecyclerView>(R.id.gameboardOpponentCards)
        opponentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        opponentRecyclerView.adapter = opponentAdapter

        val playerCardsRecyclerView = view.findViewById<RecyclerView>(R.id.boardGameSpellCard)
        playerCardsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        playerCardsRecyclerView.adapter = cardSlotAdapter

        val playerHPText = view.findViewById<TextView>(R.id.gameboardPlayerHP)
        val playerVPText = view.findViewById<TextView>(R.id.gameboardPlayerVP)
        val playerUsernameText = view.findViewById<TextView>(R.id.gameboardUsername)
        val playerAvatar = view.findViewById<ImageView>(R.id.playerAvatar)

        val gameStateView = view.findViewById<TextView>(R.id.gamestate)

        val kingAvatar = view.findViewById<ImageView>(R.id.boardgameKing)
        kingAvatar.setImageResource(viewModel.currentKing.value?.characterImageResId ?: player.characterImageResId)

        playerUsernameText.text = player.name
        playerAvatar.setImageResource(player.characterImageResId)

        val rollButton = view.findViewById<Button>(R.id.boardGameRollButton)
        val inventoryButton = view.findViewById<Button>(R.id.boardGameInventoryButton)
        val skipButton = view.findViewById<Button>(R.id.skipButton)

        rollButton.setOnClickListener {
            openCustomModal()
        }
        
        inventoryButton.setOnClickListener{
            openInventoryModal()
        }

        skipButton.setOnClickListener {
            viewModel.goToNextState()
        }

        viewModel.currentState.observe(viewLifecycleOwner) { gamestate ->
            gameStateView.text = getGameState(gamestate)
            isAIPlaying = viewModel.currentPlayer.value?.id != viewModel.player.value?.id

            rollButton.isEnabled = (gamestate == GameState.RollDiceState) && !isAIPlaying
            inventoryButton.isEnabled = (gamestate == GameState.BuyState) && !isAIPlaying
            cardSlotAdapter.updateIsAttackState((gamestate == GameState.AttackState) && !isAIPlaying)
            skipButton.isEnabled = !isAIPlaying


            viewModel.currentPlayer.value?.id?.let { opponentAdapter.updateCurrentPlayerId(it) }


            if (!isAIPlaying) {
                val borderDrawable = ContextCompat.getDrawable(view.context, R.drawable.border)
                val layers = arrayOf(playerAvatar.drawable, borderDrawable)
                val layerDrawable = LayerDrawable(layers)
                playerAvatar.setImageDrawable(layerDrawable)
            } else {
                playerAvatar.setImageResource(player.characterImageResId)
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
                    opponentAdapter.updateOpponents(diceResults!!)
                    playerHPText.text = viewModel.player.value?.healthPoints.toString()
                    playerVPText.text = viewModel.player.value?.victoryPoints.toString()
                    viewModel.goToNextState()
                }
                GameState.BuyState -> {

                }
                GameState.AttackState -> {

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
                viewModel.goToNextState()
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
        val energyCount = inventoryView.findViewById<TextView>(R.id.energyCount)
        val cardAdapter = CardAdapter(getPredifinedCards(requireView()))

        inventoryRecyclerView.adapter = cardAdapter
        inventoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        energyCount.text = viewModel.player.value?.energy.toString()

        val closeInventoryButton = inventoryView.findViewById<Button>(R.id.closeInventoryButton)

        val alertDialog = dialogBuilder.create()
        closeInventoryButton.setOnClickListener {
            alertDialog.dismiss()
            viewModel.goToNextState()
        }


        alertDialog.show()

    }

    override fun onDiceClicked(diceId: Int) {
        diceAdapter.toggleRollability(diceId)
    }

    override fun onSlotCardClick(cardPosition: Int) {
        viewModel.onCardUsed(cardPosition)

        val currentCards = (viewModel.player.value?.cards ?: emptyList()).toMutableList()

        currentCards[cardPosition] = getInitialsCards(requireView())[0]

        viewModel.player.value?.copy(cards = currentCards)?.let { viewModel.updatePlayer(it) }
    }
}