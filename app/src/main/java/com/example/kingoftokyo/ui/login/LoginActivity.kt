package com.example.kingoftokyo.ui.login

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialisez le ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Lier le ViewModel à la vue
        val characterRecyclerView = findViewById<RecyclerView>(R.id.characterRecyclerView)
        val playerNameEditText = findViewById<EditText>(R.id.playerNameEditText)

        // Observer les LiveData pour mettre à jour l'interface utilisateur
        viewModel.characters.observe(this) { characters ->
            // Mettez à jour la liste de personnages dans votre RecyclerView
        }

        viewModel.playerName.observe(this, { playerName ->
            // Mettez à jour le champ de nom du joueur
        })

        // Chargez les personnages
        viewModel.loadCharacters()

        // Écoutez les changements dans le champ de nom du joueur et mettez à jour le ViewModel lorsque cela change.
        playerNameEditText.addTextChangedListener { text ->
            viewModel.setPlayerName(text.toString())
        }
    }
}
