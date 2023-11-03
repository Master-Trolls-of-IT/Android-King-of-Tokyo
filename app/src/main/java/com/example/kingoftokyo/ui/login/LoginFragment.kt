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
import com.example.kingoftokyo.ui.login.adapter.CharacterAdapter

class LoginFragment : Fragment(), CharacterAdapter.CharacterClickListener {
    private lateinit var viewModel: LoginViewModel
    private lateinit var characterAdapter: CharacterAdapter
    private var lastClickedCharacter: PlayerCharacter? = null
    private var playerName: String = ""

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
        viewModel = LoginViewModel()

        // Bind the ViewModel to the view
        val characterRecyclerView = view.findViewById<RecyclerView>(R.id.characterRecyclerView)
        val playerNameEditText = view.findViewById<EditText>(R.id.playerNameEditText)
        val playButton = view.findViewById<Button>(R.id.playButton)

        // Set up the RecyclerView and CharacterAdapter
        characterAdapter = CharacterAdapter(emptyList(), this)
        characterRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        characterRecyclerView.adapter = characterAdapter

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
            playerName = playerNameEditText.text.toString()
            Log.d("Debug", "Nom du joueur : $playerName")
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_loginFragment2_to_gameFragment2)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCharacterClicked(character: PlayerCharacter) {
        lastClickedCharacter = character
        Log.d("Debug", "Personnage cliqué : ${lastClickedCharacter!!.name}")
    }

}