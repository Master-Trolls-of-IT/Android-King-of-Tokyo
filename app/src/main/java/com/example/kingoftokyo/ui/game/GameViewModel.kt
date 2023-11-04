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
import com.example.kingoftokyo.model.GameState
import kotlin.random.Random

class GameViewModel(private val view: View,private val selectedPlayerId: Int): ViewModel() {
    private val _opponents = MutableLiveData<List<PlayerModel>>()
    var opponents: LiveData<List<PlayerModel>> = _opponents

    private val _currentPlayer = MutableLiveData<PlayerModel>()
    val currentPlayer: LiveData<PlayerModel> get() = _currentPlayer

    private val _player = MutableLiveData<PlayerModel>()
    var player : LiveData<PlayerModel> = _player

    private val _currentState = MutableLiveData<GameState>()
    val currentState: LiveData<GameState> = _currentState

    init {
        initCharactersList(selectedPlayerId)
        startGame()
    }

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
                2 -> DiceModel("Croco Die", R.drawable.rabbit, "attack")
                3 -> DiceModel("Croco Die", R.drawable.duck, "energy")
                4 -> DiceModel("Croco Die", R.drawable.wolf, "victory1")
                5 -> DiceModel("Croco Die", R.drawable.duck, "victory2")
                6 -> DiceModel("Croco Die", R.drawable.croco, "victory3")
                else -> DiceModel("Croco Die", R.drawable.croco, "face_inconnue") // En cas de valeur inattendue
            }

            diceResults.add(result)
        }

        return diceResults
    }

    fun startGame() {
        _currentState.value = GameState.RollDiceState
    }

    fun goToNextState() {
        when (_currentState.value) {
            GameState.RollDiceState -> _currentState.value = GameState.BuyState
            GameState.BuyState -> _currentState.value = GameState.AttackState
            GameState.AttackState -> _currentState.value = GameState.ResolveDiceState
            GameState.ResolveDiceState -> _currentState.value = GameState.EndTurnState
            GameState.EndTurnState -> _currentState.value = GameState.RollDiceState
            null -> TODO()
        }
    }

    fun endTurn() {
        // Mettez à jour l'état du jeu si nécessaire (par exemple, passer au tour suivant)
        // Mettez à jour les points de victoire et de santé des joueurs
        goToNextState()

        // Vérifiez si c'est le tour du joueur ou d'un opposant
        if (player.value?.id == currentPlayer.value?.id) {
            // C'est le tour du joueur
            // Effectuez les actions du joueur ici
            // Par exemple, lancez les dés et mettez à jour les points de victoire et de santé
        } else {
            // C'est le tour d'un opposant
            // Implémentez la logique de l'opposant ici (par exemple, IA simple)
            performSimpleAI()
        }
    }

    private fun performSimpleAI() {
        // Implémentez la logique de l'IA ici (par exemple, faire toujours la même action)
        // Mettez à jour l'état du jeu en conséquence
        // Puis appelez endTurn() pour passer au tour suivant
    }


}