package com.example.kingoftokyo.ui.game

import PlayerCharacter
import PlayerModel
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.boilerplate.getPredifinedPlayerCharacter

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
}