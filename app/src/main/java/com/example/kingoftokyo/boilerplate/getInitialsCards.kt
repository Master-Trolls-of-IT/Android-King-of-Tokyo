package com.example.kingoftokyo.boilerplate

import android.view.View
import com.example.kingoftokyo.R
import com.example.kingoftokyo.model.Card

fun getInitialsCards(view: View): List<Card> {
    val initialsCard =  view.resources.getString(R.string.initial_card)
    val cardData = initialsCard.split(", ")
    val initialCard = Card(cardData[0].toInt(), cardData[1], view.resources.getIdentifier(cardData[2], "drawable", "com.example.kingoftokyo"), cardData[3], 0, cardData[4], 0)

    return listOf(initialCard, initialCard, initialCard)
}