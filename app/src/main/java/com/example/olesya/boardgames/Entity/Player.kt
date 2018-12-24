package com.example.olesya.boardgames.Entity

import android.arch.lifecycle.MutableLiveData

/**
 * Инициализация username в конструкторе!
 */
class Player constructor(var username: String = "player") {

    var score: MutableLiveData<Int> = MutableLiveData()

    var cards: MutableLiveData<ArrayList<Card>> = MutableLiveData()

    //TODO: status
    var playerStatus: String = ""

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
}