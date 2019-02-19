package com.example.olesya.boardgames.entity


class ImaginariumCard(url: String, isVisible: Boolean, val playerName: String) : Card(url, isVisible) {

    constructor(card: Card) : this(card.img, card.isVisible.value, "")
}