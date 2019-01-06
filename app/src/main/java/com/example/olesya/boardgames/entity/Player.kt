package com.example.olesya.boardgames.entity

import android.arch.lifecycle.MutableLiveData

/**
 * Инициализация username в конструкторе!
 */
class Player constructor(var username: String = "player") {

    var score: MutableLiveData<Int> = MutableLiveData()

    var cards: MutableLiveData<MutableList<Card>> = MutableLiveData()

    init {
        score.postValue(0)
        cards.postValue(ArrayList())
    }

    fun addCard(card: Card) {
        val cur = cards.value
        cur?.add(card)
        cards.postValue(cur)
    }

    fun changeScore(pts : Int) {
        score.postValue(score.value?.plus(pts))
    }

    fun removeCard(position: Int) {
        val cur = cards.value
        cur?.removeAt(position)
        cards.postValue(cur)
    }
}