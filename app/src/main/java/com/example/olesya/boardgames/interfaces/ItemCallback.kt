package com.example.olesya.boardgames.interfaces

import com.example.olesya.boardgames.entity.Card

interface ItemCallback {

    fun onSelectedCardEvent(username: Card)

    fun onUserTurnFinished(card: Card)

    fun onAddUserChoice(clientName: String, currentChoice: Int)
}