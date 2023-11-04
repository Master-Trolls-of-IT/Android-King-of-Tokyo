package com.example.kingoftokyo.model

sealed class GameState {
    object RollDiceState : GameState()
    object BuyState : GameState()
    object AttackState : GameState()
    object ResolveDiceState : GameState()
    object EndTurnState : GameState()
}
