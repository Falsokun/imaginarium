package com.example.olesya.boardgames.entity

import java.util.ArrayList

class ImaginariumCard(url: String, isVisible: Boolean) : Card(url, isVisible) {

    constructor(img: String, isVisible: Boolean, playerName: String) : this(img, isVisible) {
        this.playerName = playerName
    }

    constructor(randomCard: Card) : this(randomCard.img, randomCard.isVisible.value)

    /**
     * Array of user names, who voted to this card
     */
    val votes = ArrayList<String>()

    /**
     * Player name whom this card belongs to
     */
    var playerName: String? = null
}