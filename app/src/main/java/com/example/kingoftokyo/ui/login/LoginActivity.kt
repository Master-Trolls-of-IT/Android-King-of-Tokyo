package com.example.kingoftokyo.ui.login

import PlayerCharacter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.ui.login.adapter.CharacterAdapter

class LoginActivity : AppCompatActivity(), CharacterAdapter.CharacterClickListener {
    private lateinit var viewModel: LoginViewModel
    private lateinit var characterAdapter: CharacterAdapter
    private var lastClickedCharacter: PlayerCharacter? = null // Variable pour stocker le dernier personnage cliqué
    private var playerName: String = ""

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Bind the ViewModel to the view
        val characterRecyclerView = findViewById<RecyclerView>(R.id.characterRecyclerView)
        val playerNameEditText = findViewById<EditText>(R.id.playerNameEditText)
        val playButton = findViewById<Button>(R.id.playButton) // Référence au bouton "Jouer"


        // Set up the RecyclerView and CharacterAdapter
        characterAdapter = CharacterAdapter(emptyList(), this) // Pass LoginActivity as the CharacterClickListener
        characterRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        characterRecyclerView.adapter = characterAdapter

        // Observe the LiveData in the ViewModel and update the adapter when data changes
        viewModel.characters.observe(this, { characters ->
            characterAdapter.updateCharacter(characters)
        })

        // Load the characters from ViewModel
        viewModel.loadCharacters()

        playerNameEditText.addTextChangedListener { text ->
            viewModel.setPlayerName(text.toString())
        }

        playButton.setOnClickListener {
            playerName = playerNameEditText.text.toString()
            Log.d("Debug", "Nom du joueur : $playerName")
        }
    }

    override fun onCharacterClicked(character: PlayerCharacter) {
        lastClickedCharacter = character
        Log.d("Debug", "Personnage cliqué : ${lastClickedCharacter!!.name}");
    }
}
