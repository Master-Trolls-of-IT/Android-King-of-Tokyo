package com.example.kingoftokyo.ui.game

import DiceAdapter
import PlayerCharacter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter
import com.example.kingoftokyo.model.DiceModel
import com.example.kingoftokyo.ui.game.adapter.OpponentAdapter

class GameActivity: AppCompatActivity() {
    private lateinit var viewModel: GameViewModel
    lateinit var selectedCharacter : PlayerCharacter
    lateinit var opponentAdapter: OpponentAdapter
    lateinit var diceAdapter: DiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_board)

        selectedCharacter = PlayerCharacter(1, "Croco Feroce", R.drawable.croco)

        viewModel = GameViewModel(this)
        viewModel.initCharactersList(selectedCharacter.id)
        opponentAdapter = OpponentAdapter(viewModel.opponents.value!!)

        val opponentRecyclerView = findViewById<RecyclerView>(R.id.gameboardOpponentCards)
        opponentRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        opponentRecyclerView.adapter = opponentAdapter

        val playerHPText = findViewById<TextView>(R.id.gameboardPlayerHP)
        val playerVPText = findViewById<TextView>(R.id.gameboardPlayerVP)
        val playerUsernameText = findViewById<TextView>(R.id.gameboardUsername)

        fun openCustomModal() {
            val dialogBuilder = AlertDialog.Builder(this)
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
            diceRecyclerView.layoutManager = GridLayoutManager(this, 3)

            val closeButton = dialogView.findViewById<Button>(R.id.closeButton)
            val alertDialog = dialogBuilder.create()

            closeButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        val rollButton = findViewById<Button>(R.id.boardGameRollButton)

        rollButton.setOnClickListener {
            openCustomModal()
        }

        playerUsernameText.text = selectedCharacter.name
    }
}