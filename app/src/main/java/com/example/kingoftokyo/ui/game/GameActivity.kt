package com.example.kingoftokyo.ui.game

import PlayerCharacter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter
import com.example.kingoftokyo.ui.game.adapter.OpponentAdapter

class GameActivity: AppCompatActivity() {
    private lateinit var viewModel: GameViewModel
    lateinit var selectedCharacter : PlayerCharacter
    lateinit var opponentAdapter: OpponentAdapter

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

        playerUsernameText.text = selectedCharacter.name
    }
}