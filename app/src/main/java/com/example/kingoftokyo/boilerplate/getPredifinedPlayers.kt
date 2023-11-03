package com.example.kingoftokyo.boilerplate

import PlayerCharacter
import android.content.Context
import android.content.res.Resources
import android.view.View
import com.example.kingoftokyo.R

fun getPredifinedPlayerCharacter(view: View): List<PlayerCharacter> {
    val predefinedPlayers =  view.resources.getStringArray(R.array.predefined_players)
    val playersList = predefinedPlayers.map {
        val playerData = it.split(", ")
        PlayerCharacter(playerData[0].toInt(), playerData[1], view.resources.getIdentifier(playerData[2], "drawable", "com.example.kingoftokyo"))
    }
    return playersList
}