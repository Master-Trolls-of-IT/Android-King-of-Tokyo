package com.example.kingoftokyo.boilerplate

import android.view.View
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.Card

fun getPredifinedCards(view: View): List<Card> {
    val predefinedCard =  view.resources.getStringArray(R.array.predefined_cards)
    val cardList = predefinedCard.map {
        val cardData = it.split(", ")
        Card(cardData[0].toInt(), cardData[1], view.resources.getIdentifier(cardData[2], "drawable", "com.example.kingoftokyo"), cardData[3], cardData[4].toInt(), cardData[5], cardData[6].toInt())
    }
    return cardList
}