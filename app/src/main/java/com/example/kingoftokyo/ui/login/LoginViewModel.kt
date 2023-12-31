package com.example.kingoftokyo.ui.login
import PlayerCharacter
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.R
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter

class LoginViewModel(private var view: View) : ViewModel() {
    // LiveData pour les personnages disponibles

    private var _characters = MutableLiveData<List<PlayerCharacter>>()
    var characters: LiveData<List<PlayerCharacter>> = _characters


    // LiveData pour le nom du joueur
    private val _playerName = MutableLiveData<String>()
    val playerName: LiveData<String> = _playerName

    // Méthode pour initialiser les données des personnages

    fun loadCharacters(){
        val charactersList = getPredifinedPlayerCharacter(view)
        _characters.value = charactersList
    }

    // Méthode pour définir le nom du joueur
    fun setPlayerName(name: String) {
        _playerName.value = name
    }
}
