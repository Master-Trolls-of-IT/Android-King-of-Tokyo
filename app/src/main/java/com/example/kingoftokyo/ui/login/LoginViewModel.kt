package com.example.kingoftokyo.ui.login
import PlayerCharacter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.R

class LoginViewModel : ViewModel() {
    // LiveData pour les personnages disponibles

    private var _characters = MutableLiveData<List<PlayerCharacter>>()
    var characters: LiveData<List<PlayerCharacter>> = _characters


    // LiveData pour le nom du joueur
    private val _playerName = MutableLiveData<String>()
    val playerName: LiveData<String> = _playerName

    // Méthode pour initialiser les données des personnages

    fun loadCharacters(){
        val charactersList = listOf(
            PlayerCharacter(1, "Croco Feroce", R.drawable.croco),
            PlayerCharacter(2, "Coiin Coin", R.drawable.duck),
            PlayerCharacter(3, "Lapin enragé", R.drawable.rabbit),
            PlayerCharacter(4, "Loup méchant", R.drawable.wolf),

            // Ajoutez les autres personnages
        )
        _characters.value = charactersList
    }

    // Méthode pour définir le nom du joueur
    fun setPlayerName(name: String) {
        _playerName.value = name
    }
}
