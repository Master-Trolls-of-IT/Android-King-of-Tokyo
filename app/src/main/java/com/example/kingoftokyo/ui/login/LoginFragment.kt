package com.example.kingoftokyo.ui.login

import PlayerCharacter
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter
import com.example.kingoftokyo.ui.login.adapter.CharacterAdapter

class LoginFragment : Fragment(), CharacterAdapter.CharacterClickListener {
    private lateinit var viewModel: LoginViewModel
    private lateinit var characterAdapter: CharacterAdapter
    private var lastClickedCharacter: PlayerCharacter? = null // Variable pour stocker le dernier personnage cliqué
    private var playerName: String = "" // Nom par défaut du joueur
    private lateinit var defaultCharacter : PlayerCharacter // Personnage par défaut

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = LoginViewModel(view)

        // Bind the ViewModel to the view
        val characterRecyclerView = view.findViewById<RecyclerView>(R.id.characterRecyclerView)
        val playerNameEditText = view.findViewById<EditText>(R.id.playerNameEditText)
        val playButton = view.findViewById<Button>(R.id.playButton)

        // Set up the RecyclerView and CharacterAdapter
        characterAdapter = CharacterAdapter(emptyList(), this)
        characterRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        characterRecyclerView.adapter = characterAdapter

        defaultCharacter = getPredifinedPlayerCharacter(view)[0]

        // Observe the LiveData in the ViewModel and update the adapter when data changes
        viewModel.characters.observe(viewLifecycleOwner) { characters ->
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

            Log.d("lastClickedCharacter",lastClickedCharacter.toString())

            // check si un personnage a été sélectionné, sinon on attribue le personnage par défaut
            if( lastClickedCharacter == null) {
                lastClickedCharacter = defaultCharacter
                playerName = defaultCharacter.name
            }

            Log.d("lastClickedCharacter",lastClickedCharacter.toString())

            val bundle = Bundle()
            bundle.putParcelable("selectedCharacter", lastClickedCharacter)
            bundle.putString("playerName", playerName)

            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_loginFragment2_to_gameFragment2, bundle)
        }
    }

    override fun onCharacterClicked(character: PlayerCharacter) {
        lastClickedCharacter = character
    }

}