package com.example.kingoftokyo.ui.game

import CardAdapter
import DiceAdapter
import PlayerCharacter
import PlayerModel
import android.annotation.SuppressLint
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
import com.example.kingoftokyo.R.*
import com.example.kingoftokyo.model.Card
import com.example.kingoftokyo.model.DiceModel
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

        return inflater.inflate(layout.fragment_game, container, false)
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

        val rollButton = view.findViewById<Button>(R.id.boardGameRollButton)
        val inventoryButton = view.findViewById<Button>(R.id.boardGameInventoryButton)

        rollButton.setOnClickListener {
            openCustomModal()
        }
        inventoryButton.setOnClickListener{
            openInventoryModal()
        }
    }

    fun openCustomModal() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(layout.dice_roll_modal_layout, null)
        dialogBuilder.setView(dialogView)

        val diceRecyclerView = dialogView.findViewById<RecyclerView>(R.id.diceRecyclerView)
        val diceAdapter = DiceAdapter(
            listOf(
                DiceModel("Croco Die", drawable.croco, "heal"),
                DiceModel("Croco Die", drawable.croco, "attack"),
                DiceModel("Croco Die", drawable.croco, "energy"),
                DiceModel("Croco Die", drawable.croco, "victory1"),
                DiceModel("Croco Die", drawable.croco, "victory2"),
                DiceModel("Croco Die", drawable.croco, "victory3")
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
}