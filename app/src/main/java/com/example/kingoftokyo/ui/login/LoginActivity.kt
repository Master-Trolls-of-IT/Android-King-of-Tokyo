package com.example.kingoftokyo.ui.login

import CharacterAdapter
import PlayerCharacter
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    var character = listOf<PlayerCharacter>()
    val characterAdapter = CharacterAdapter(character)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialisez le ViewModel
        viewModel = LoginViewModel()

        // Lier le ViewModel à la vue
        val characterRecyclerView = findViewById<RecyclerView>(R.id.characterRecyclerView)
        characterRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val playerNameEditText = findViewById<EditText>(R.id.playerNameEditText)
        characterRecyclerView.adapter = characterAdapter
        viewModel.loadCharacters()
        character = viewModel.characters.value!!
        characterAdapter.updateCharacter(character)

       characterAdapter.notifyDataSetChanged()
        playerNameEditText.addTextChangedListener { text ->
            viewModel.setPlayerName(text.toString())
        }
    }
}
