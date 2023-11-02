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
    private var playerName: String = "" // Nom par défaut du joueur
    private val defaultCharacter = PlayerCharacter(0, "Croco Feroce", R.drawable.croco) // Personnage par défaut


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Bind the ViewModel to the view
        val characterRecyclerView = findViewById<RecyclerView>(R.id.characterRecyclerView)
        val playerNameEditText = findViewById<EditText>(R.id.playerNameEditText)
        val playButton = findViewById<Button>(R.id.playButton)


        // Set up the RecyclerView and CharacterAdapter
        characterAdapter = CharacterAdapter(emptyList(), this) // Pass LoginActivity as the CharacterClickListener
        characterRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        characterRecyclerView.adapter = characterAdapter

        // Observe the LiveData in the ViewModel and update the adapter when data changes
        viewModel.characters.observe(this) { characters ->
            characterAdapter.updateCharacter(characters)
        }

        // Load the characters from ViewModel
        viewModel.loadCharacters()

        playerNameEditText.addTextChangedListener { text ->
            viewModel.setPlayerName(text.toString())
        }

        playButton.setOnClickListener {
            // TODO: Pass the player name and selected character to the next activity

            val enteredName = playerNameEditText.text.toString()

            // Si le nom du joueur est vide et qu'aucun personnage n'a été sélectionné, on attribue le nom par défaut au joueur
            if (enteredName.isEmpty() && lastClickedCharacter == null) {
                playerName = "Player 1"
            }
            else if (enteredName.isEmpty() && lastClickedCharacter != null) {
                playerName = lastClickedCharacter?.name.toString()
            }
            else if (enteredName.isNotEmpty()) {
                playerName = enteredName
            }

            // check si un personnage a été sélectionné, sinon on attribue le personnage par défaut
            if( lastClickedCharacter == null) {
                lastClickedCharacter = defaultCharacter
            }

            Log.d("Debug", "Personnage sélectionné : ${lastClickedCharacter!!.name}")
            Log.d("Debug", "Nom du joueur : $playerName")
        }
    }

    override fun onCharacterClicked(character: PlayerCharacter) {
        lastClickedCharacter = character
        Log.d("Debug", "Personnage cliqué : ${lastClickedCharacter!!.name}");
    }
}
