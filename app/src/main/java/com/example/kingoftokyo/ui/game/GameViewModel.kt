package com.example.kingoftokyo.ui.game

import PlayerCharacter
import PlayerModel
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.R
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter
import com.example.kingoftokyo.model.DiceModel
import com.example.kingoftokyo.model.GameState
import kotlin.random.Random

class GameViewModel(private var view: View,private var selectedPlayerId: Int): ViewModel() {
    private val _opponents = MutableLiveData<List<PlayerModel>>()
    val opponents: LiveData<List<PlayerModel>> get() = _opponents

    private val _currentPlayer = MutableLiveData<PlayerModel>()
    val currentPlayer: LiveData<PlayerModel> get() = _currentPlayer

    private val _player = MutableLiveData<PlayerModel>()
    val player : LiveData<PlayerModel> get() = _player

    private val _currentKing = MutableLiveData<PlayerModel>()
    val currentKing: LiveData<PlayerModel> get() = _currentKing

    private val _currentState = MutableLiveData<GameState>()
    val currentState: LiveData<GameState> get() = _currentState

    init {
        initCharactersList(selectedPlayerId)
        val isKingInOpponents = _opponents.value?.filter { it.id == 1 }
        if (isKingInOpponents?.size == 0) {
            _currentKing.value = _player.value
        } else {
            _currentKing.value = isKingInOpponents?.get(0)
        }
        _currentPlayer.value = _currentKing.value
        startGame()
    }

    fun initCharactersList(selectedCharacterId: Int) {
        val predifinedCharacters = getPredifinedPlayerCharacter(view)

        val mappedPredifinedCharacters = predifinedCharacters.map {
            PlayerModel( it.id, it.name, it.characterImageResId, 0, 10, 0, emptyList())
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

    fun rollDices(diceList: List<DiceModel>): List<DiceModel> {

        return diceList.map {
            if (it.isRollable) {
                val randomValue = Random.nextInt(1, 7)
                val result = when (randomValue) {
                    1 -> DiceModel(it.id, "Soin", R.drawable.heal, "heal", true)
                    2 -> DiceModel(it.id, "Attaque", R.drawable.attack, "attack", true)
                    3 -> DiceModel(it.id, "Energie", R.drawable.energy, "energy", true)
                    4 -> DiceModel(it.id, "Victoire1", R.drawable.victory1, "victory1", true)
                    5 -> DiceModel(it.id, "Victoire2", R.drawable.victory2, "victory2", true)
                    6 -> DiceModel(it.id, "Victoire3", R.drawable.victory3, "victory3", true)
                    else -> DiceModel(it.id, "LoNoSe", R.drawable.face_inconnue, "face_inconnue", true) // En cas de valeur inattendue
                }
                result
            } else it
        }
    }

    fun calculateDiceResults(diceResults: List<DiceModel>): List<PlayerModel>? {
        var victory1Count = 0
        var victory2Count = 0
        var victory3Count = 0
        for (dice in diceResults) {
            when (dice.value) {
                "attack" -> {
                    // Logique pour gérer le résultat "attaque"
                    if (currentPlayer.value?.id == currentKing.value?.id) {
                        _opponents.value = _opponents.value?.map {
                            if (it.id == currentKing.value?.id) it
                            else PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints, it.healthPoints - 1, it.energy, it.cards)
                        }

                        if (player.value?.id != currentKing.value?.id) {
                            _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!!, _player.value?.healthPoints!! - 1, _player.value?.energy!!, _player.value?.cards!!)
                        }
                    } else {
                        _opponents.value = _opponents.value?.map {
                            if (it.id == currentKing.value?.id) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints, it.healthPoints - 1, _player.value?.energy!!, it.cards)
                            else it
                        }

                        if (player.value?.id == currentKing.value?.id) {
                            _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!!, _player.value?.healthPoints!! - 1, _player.value?.energy!!, _player.value?.cards!!)
                        }
                    }
                }
                "heal" -> {
                    // Logique pour gérer le résultat "soin"
                    if (currentPlayer.value?.id == currentKing.value?.id) {
                        _opponents.value = _opponents.value?.map {
                            if (it.id == currentPlayer.value?.id && it.healthPoints < 10) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints, it.healthPoints + 1, it.energy, it.cards)
                            else it
                        }

                        if (player.value?.id == currentPlayer.value?.id && player.value?.healthPoints!! < 10) {
                            _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!!, _player.value?.healthPoints!! + 1, _player.value?.energy!!, _player.value?.cards!!)
                        }
                    }
                }
                "energy" -> {
                    // Logique pour gérer le résultat "énergie"
                    _opponents.value = _opponents.value?.map {
                        if (it.id == currentPlayer.value?.id) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints, it.healthPoints, it.energy + 1, it.cards!!)
                        else it
                    }

                    if (player.value?.id == currentPlayer.value?.id) {
                        _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!!, _player.value?.healthPoints!!, _player.value?.energy!! + 1, _player.value?.cards!!)
                    }
                }
                "victory1" -> {
                    // Logique pour gérer les résultats de victoire 1
                    victory1Count += 1
                }
                "victory2" -> {
                    // Logique pour gérer les résultats de victoire 1
                    victory2Count += 1
                }
                "victory3" -> {
                    // Logique pour gérer les résultats de victoire 1
                    victory3Count += 1
                }
                else -> {
                    // Logique pour gérer tout autre résultat
                }
            }
        }
        // Logique pour les points de victoires
        if (victory1Count >= 3) {
            var victoryCount = 1
            if (victory1Count > 3) {
                victoryCount += victory1Count - 3
            }
            victoryCount += victory2Count * 2
            victoryCount += victory3Count * 3
            _opponents.value = _opponents.value?.map {
                if (it.id == currentPlayer.value?.id) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints + victoryCount, it.healthPoints, it.energy + 1, it.cards)
                else it
            }

            if (player.value?.id == currentPlayer.value?.id) {
                Log.d("player id1", _player.value?.id.toString())
                Log.d("current id",currentPlayer.value?.id.toString())
                _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!! + victoryCount, _player.value?.healthPoints!!, _player.value?.energy!! + 1, _player.value?.cards!!)
            }
        } else if (victory2Count >= 3) {
            var victoryCount = 2
            if (victory2Count > 3) {
                victoryCount += (victory2Count - 3) * 2
            }
            victoryCount += victory1Count
            victoryCount += victory3Count * 3
            _opponents.value = _opponents.value?.map {
                if (it.id == currentPlayer.value?.id) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints + victoryCount, it.healthPoints, it.energy + 1, it.cards)
                else it
            }

            if (player.value?.id == currentPlayer.value?.id) {
                Log.d("player id2", _player.value?.id.toString())
                Log.d("current id",currentPlayer.value?.id.toString())
                _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!! + victoryCount, _player.value?.healthPoints!!, _player.value?.energy!! + 1, _player.value?.cards!!)
            }
        } else if (victory3Count >= 3) {
            var victoryCount = 3
            if (victory3Count > 3) {
                victoryCount += (victory1Count - 3) * 3
            }
            victoryCount += victory2Count * 2
            victoryCount += victory1Count
            _opponents.value = _opponents.value?.map {
                if (it.id == currentPlayer.value?.id) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints + victoryCount, it.healthPoints, it.energy + 1, it.cards)
                else it
            }

            if (player.value?.id == currentPlayer.value?.id) {
                _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!! + victoryCount, _player.value?.healthPoints!!, _player.value?.energy!! + 1, _player.value?.cards!!)
            }
        }

        // Point de victoire pour le king
        _opponents.value = _opponents.value?.map {
            if (it.id == currentKing.value?.id) PlayerModel(it.id, it.name, it.characterImageResId, it.victoryPoints, it.healthPoints , _player.value?.energy!! + 1, _player.value?.cards!!)
            else it
        }

        if (player.value?.id == currentKing.value?.id) {
            _player.value = PlayerModel(_player.value?.id!!, _player.value?.name!!, _player.value?.characterImageResId!!, _player.value?.victoryPoints!!, _player.value?.healthPoints!!, _player.value?.energy!! + 1, _player.value?.cards!!)
        }

        return opponents.value
    }


    fun startGame() {
        _currentState.value = GameState.RollDiceState
    }

    fun goToNextState() {
        when (_currentState.value) {
            GameState.RollDiceState -> _currentState.value = GameState.ResolveDiceState
            GameState.ResolveDiceState -> _currentState.value = GameState.BuyState
            GameState.BuyState -> _currentState.value = GameState.AttackState
            GameState.AttackState -> _currentState.value = GameState.EndTurnState
            GameState.EndTurnState -> _currentState.value = GameState.RollDiceState
            null -> TODO()
        }
    }

    fun endTurn() {

        var currentPlayerId = _currentPlayer.value?.id ?: 1
        Log.d("endTurn currentPlayerId", currentPlayerId.toString())
        currentPlayerId = currentPlayerId + 1
        if (currentPlayerId == 5){
            currentPlayerId = 1
        }
        Log.d("endTurn NewcurrentPlayerId", currentPlayerId.toString())
        if (_player.value?.id == currentPlayerId) {
            Log.d("isNewPlayer player", "yes")
            _currentPlayer.value = _player.value
        } else {
            Log.d("isNewPlayer player", "Nop")
            _currentPlayer.value = _opponents.value?.find { it.id == currentPlayerId }
            Log.d("final opponent", _currentPlayer.value?.id.toString())
        }
        goToNextState()
    }


}