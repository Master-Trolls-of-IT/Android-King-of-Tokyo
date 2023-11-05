package com.example.kingoftokyo.model

data class DiceModel(
    val id: Int,
    val name: String,
    val imageResId: Int,
    val value: String,
    var isRollable: Boolean
)