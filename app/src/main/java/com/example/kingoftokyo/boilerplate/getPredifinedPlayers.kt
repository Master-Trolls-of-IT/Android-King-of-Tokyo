package com.example.kingoftokyo.boilerplate

import PlayerCharacter
import android.content.Context
import android.content.res.Resources
import com.example.kingoftokyo.R

fun getPredifinedPlayerCharacter(context: Context): List<PlayerCharacter> {
    val predefinedPlayers =  context.resources.getStringArray(R.array.predefined_players)
    val playersList = predefinedPlayers.map {
        val playerData = it.split(", ")
        PlayerCharacter(playerData[0].toInt(), playerData[1], context.resources.getIdentifier(playerData[2], "drawable", "com.example.kingoftokyo"))
    }
    return playersList
}