package com.example.kingoftokyo.boilerplate

import com.example.kingoftokyo.model.GameState

fun getGameState(gameState: GameState) : String {
    return when (gameState) {
        GameState.RollDiceState -> "Lancement de dés"
        GameState.ResolveDiceState -> "Calcul des dés"
        GameState.BuyState -> "Achat des cartes"
        GameState.AttackState -> "Utilisation des cartes"
        GameState.EndTurnState -> "Fin du tour"
    }
}