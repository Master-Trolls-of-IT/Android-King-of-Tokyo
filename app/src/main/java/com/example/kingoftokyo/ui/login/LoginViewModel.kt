package com.example.kingoftokyo.ui.login
import PlayerCharacter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.R

class LoginViewModel : ViewModel() {
    // LiveData pour les personnages disponibles
    private val _characters = MutableLiveData<List<PlayerCharacter>>()
    val characters: LiveData<List<PlayerCharacter>> = _characters

    // LiveData pour le nom du joueur
    private val _playerName = MutableLiveData<String>()
    val playerName: LiveData<String> = _playerName

    // Méthode pour initialiser les données des personnages
    fun loadCharacters() {
        val charactersList = listOf(
            PlayerCharacter(1, "Personnage 1"),
            PlayerCharacter(2, "Personnage 2"),
            PlayerCharacter(3, "Personnage 3"),
            PlayerCharacter(4, "Personnage 4"),
            PlayerCharacter(5, "Personnage 5"),
            // Ajoutez les autres personnages
        )
        _characters.value = charactersList
    }

    // Méthode pour définir le nom du joueur
    fun setPlayerName(name: String) {
        _playerName.value = name
    }
}
