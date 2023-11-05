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
import kotlin.random.Random

class GameFragment : Fragment(), DiceAdapter.DiceClickListener, CardAdapter.OnCardClickListener, CardSlotAdpater.CardSlotClickListener {
    private lateinit var viewModel: GameViewModel
    private lateinit var player: PlayerModel
    private lateinit var opponentAdapter: OpponentAdapter
    private lateinit var cardSlotAdapter: CardSlotAdpater
    private lateinit var diceAdapter: DiceAdapter
    private lateinit var diceList: List<DiceModel>
    private var isAIPlaying: Boolean = false
    private var aiDiceRolled = false
    private var cardList: List<Card> = emptyList()
    private var aiShopOpen = false
    private var aiKingOpen = false

    private lateinit var playerVPText: TextView
    private lateinit var playerHPText: TextView

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

        playerHPText = view.findViewById<TextView>(R.id.gameboardPlayerHP)
        playerVPText = view.findViewById<TextView>(R.id.gameboardPlayerVP)
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


        cardList = getPredifinedCards(requireView())

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
            kingAvatar.setImageResource(viewModel.currentKing.value?.characterImageResId ?: player.characterImageResId)
            skipButton.isEnabled = !isAIPlaying

            if (viewModel.currentPlayer.value?.healthPoints == 0) {
                viewModel.endTurn()
            }

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
                    aiShopOpen = false
                    if (isAIPlaying) {
                        openInventoryModal()
                    }
                }
                GameState.AttackState -> {
                    if (isAIPlaying) {
                        viewModel.goToNextState()
                    }
                }
                GameState.EndTurnState -> {
                    aiKingOpen = false
                    openTokyoModal()
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
                delay(700)
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

                delay(1200)

                diceResults = viewModel.rollDices(diceAdapter.diceList)
                remainingRollsValue--
                remainingRolls.text = remainingRollsValue.toString()
                if (remainingRollsValue == 0) {
                    rollButton.isEnabled = false
                }
                diceList = diceResults
                diceAdapter.updateDice(diceResults)

                delay(1200)

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
        val cardAdapter = CardAdapter(cardList, this)

        inventoryRecyclerView.adapter = cardAdapter
        inventoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        energyCount.text = viewModel.currentPlayer.value?.energy.toString()

        val closeInventoryButton = inventoryView.findViewById<Button>(R.id.closeInventoryButton)
        val buyCardButton = inventoryView.findViewById<Button>(R.id.BuyButton)


        val alertDialog = dialogBuilder.create()
        if (!isAIPlaying) {
            alertDialog.show()
            closeInventoryButton.setOnClickListener {
                alertDialog.dismiss()
                viewModel.goToNextState()
            }
        } else if (!aiShopOpen) {
            viewLifecycleOwner.lifecycleScope.launch {
                aiShopOpen = true
                delay(1500)
                alertDialog.show()

                closeInventoryButton.isEnabled = false

                delay(1500)
                viewModel.goToNextState()
                alertDialog.dismiss()
            }
        }
    }

    fun openTokyoModal() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val tokyoModal = inflater.inflate(layout.tokyo_modal, null)
        dialogBuilder.setView(tokyoModal)

        val title = tokyoModal.findViewById<TextView>(R.id.tokyoModalTitle)
        val confirmButton = tokyoModal.findViewById<TextView>(R.id.tokyoModalConfirm)
        val denyButton = tokyoModal.findViewById<TextView>(R.id.tokyoModalDeny)

        title.text = buildString {
            append("Voulez-vous sortir de tokyo ")
            append(viewModel.currentKing.value?.name)
            append("?")
        }

        val alertDialog = dialogBuilder.create()

        Log.d("damages ?", viewModel.isKingTakenDamages.toString())

        if (viewModel.canGoOutOfTokyo || viewModel.isKingTakenDamages) {
            if (!viewModel.isKingAI) {
                alertDialog.show()
                confirmButton.setOnClickListener {
                    viewModel.nextKing()
                    alertDialog.dismiss()
                    viewModel.endTurn()
                }

                denyButton.setOnClickListener {
                    alertDialog.dismiss()
                    viewModel.endTurn()
                }
            } else if (!aiKingOpen) {
                viewLifecycleOwner.lifecycleScope.launch {
                    aiKingOpen = true
                    confirmButton.isEnabled = false
                    denyButton.isEnabled = false
                    delay(1000)

                    alertDialog.show()

                    delay(2000)

                    val randomValue = Random.nextInt(1, 2)

                    if (randomValue == 1) {
                        viewModel.nextKing()
                    }

                    alertDialog.dismiss()
                    viewModel.endTurn()
                }
            }
        } else {
            viewModel.endTurn()
        }

    }

    override fun onDiceClicked(diceId: Int) {
        diceAdapter.toggleRollability(diceId)
    }

    override fun onCardClicked(cardId: Int) {
        val card = cardList.find { it.id == cardId }
        if (card != null) {
            val currentPlayer = viewModel.currentPlayer.value
            val (canBuy, errorMessage) = canBuyCard(card, currentPlayer)
            if (canBuy) {
                val confirmationDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Confirmation d'achat")
                    .setMessage("Voulez-vous vraiment acheter ${card.name} pour ${card.cost} énergie ?")
                    .setPositiveButton("Oui") { dialog, _ ->
                        dialog.dismiss()
                        buyCard(card)
                        val energyCount = view?.findViewById<TextView>(R.id.energyCount)
                        energyCount?.text = currentPlayer?.energy.toString()
                        //cardSlotAdapter.updateCards(currentPlayer?.inventory)
                    }
                    .setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
                    .create()
                confirmationDialog.show()
            } else {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Erreur d'achat")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                alertDialog.show()
            }
        }
    }

    private fun canBuyCard(card: Card, currentPlayer: PlayerModel?): Pair<Boolean, String> {
        if (currentPlayer == null) {
            return Pair(false, "Le joueur n'est pas défini.")
        }
        if (currentPlayer.energy < card.cost) {
            return Pair(false, "Vous n'avez pas suffisamment d'énergie pour acheter cette carte. Il vous manque : ${card.cost - currentPlayer.energy} énergie.")
        }
        if (currentPlayer.cards.contains(card)) {
            return Pair(false, "Vous avez déjà acheté cette carte.")
        }
        if (currentPlayer.cards.count { it.id != 0 } >= 3) {
            return Pair(false, "Vous avez atteint la limite de 3 cartes.")
        }
        return Pair(true, "Aucun message d'erreur")  // Aucune erreur
    }


    private fun buyCard(card: Card) {
        viewModel.updateCardPlayer(card, isAIPlaying)
        cardSlotAdapter.updateCards(viewModel.currentPlayer.value?.cards!!)
        val message = "Vous venez d'acheter ${card.name}"
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Achat effectué")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        alertDialog.show()
    }



    override fun onSlotCardClick(cardPosition: Int) {
        viewModel.onCardUsed(cardPosition)

        val currentCards = (viewModel.player.value?.cards ?: emptyList()).toMutableList()

        currentCards[cardPosition] = getInitialsCards(requireView())[0]

        viewModel.player.value?.copy(cards = currentCards)?.let { viewModel.updatePlayer(it) }
        cardSlotAdapter.updateCards(viewModel.player.value?.cards!!)
        opponentAdapter.updateOpponents(viewModel.opponents.value!!)

        playerHPText.text = viewModel.player.value?.healthPoints.toString()
        playerVPText.text = viewModel.player.value?.victoryPoints.toString()
    }
}