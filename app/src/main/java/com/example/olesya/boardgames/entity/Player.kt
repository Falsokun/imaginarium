package com.example.olesya.boardgames.entity

import android.arch.lifecycle.MutableLiveData
import io.reactivex.subjects.BehaviorSubject

/**
 * Инициализация username в конструкторе!
 */
class Player constructor(var username: String) {

    val score: BehaviorSubject<Int> = BehaviorSubject.create<Int>()
    var cards: MutableLiveData<MutableList<Card>> = MutableLiveData()

    init {
        score.onNext(0)
        cards.postValue(ArrayList())
    }

    fun addCard(card: Card) {
        val cur = cards.value
        cur?.add(card)
        cards.postValue(cur)
    }

    fun changeScore(pts : Int) {
        score.onNext(pts)
    }

    fun removeCard(position: Int) {
        val cur = cards.value
        cur?.removeAt(position)
        cards.postValue(cur)
    }
}