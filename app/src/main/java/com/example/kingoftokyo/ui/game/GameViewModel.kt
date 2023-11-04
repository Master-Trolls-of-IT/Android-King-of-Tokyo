package com.example.kingoftokyo.ui.game

import PlayerCharacter
import PlayerModel
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.R
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter
import com.example.kingoftokyo.model.DiceModel
import kotlin.random.Random

class GameViewModel(private val view: View): ViewModel() {
    private val _opponents = MutableLiveData<List<PlayerModel>>()
    var opponents: LiveData<List<PlayerModel>> = _opponents

    private val _player = MutableLiveData<PlayerModel>()
    var player : LiveData<PlayerModel> = _player

    fun initCharactersList(selectedCharacterId: Int) {
        val predifinedCharacters = getPredifinedPlayerCharacter(view)

        val mappedPredifinedCharacters = predifinedCharacters.map {
            PlayerModel(it.name, it.id, it.characterImageResId, 0, 20)
        }

        _opponents.value = mappedPredifinedCharacters.filter { it.id != selectedCharacterId }
        _player.value = mappedPredifinedCharacters.filter { it.id == selectedCharacterId }[0]
    }

    fun rollDice(): String {
        val random = Random.nextInt(1, 7) // Génère un nombre aléatoire entre 1 et 6

        return when (random) {
            1 -> "heal"
            2 -> "attack"
            3 -> "energy"
            4 -> "victory1"
            5 -> "victory2"
            else -> "victory3"
        }
    }

    fun rollMultipleDice(numberOfDice: Int): List<DiceModel> {
        val diceResults = mutableListOf<DiceModel>()

        for (i in 1..numberOfDice) {
            val randomValue = Random.nextInt(1, 7) // Génère un nombre aléatoire entre 1 et 6

            // Mappez le nombre aléatoire à une face du dé
            val result = when (randomValue) {
                1 -> DiceModel("Croco Die", R.drawable.croco, "heal")
                2 -> DiceModel("Croco Die", R.drawable.croco, "attack")
                3 -> DiceModel("Croco Die", R.drawable.croco, "energy")
                4 -> DiceModel("Croco Die", R.drawable.croco, "victory1")
                5 -> DiceModel("Croco Die", R.drawable.croco, "victory2")
                6 -> DiceModel("Croco Die", R.drawable.croco, "victory3")
                else -> DiceModel("Croco Die", R.drawable.croco, "face_inconnue") // En cas de valeur inattendue
            }

            diceResults.add(result)
        }

        return diceResults
    }

}