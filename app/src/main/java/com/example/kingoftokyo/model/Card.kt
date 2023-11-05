package com.example.kingoftokyo.model

data class Card (
    val id: Int,
    val name: String,
    val imageResId: Int,
    val description: String,
    val cost: Int,
    val type: String,
    val effect: Int
)