package com.example.olesya.boardgames.Entity

import io.reactivex.subjects.BehaviorSubject


open class Card(val img: String, isVisible: Boolean) {

    val isVisible: BehaviorSubject<Boolean> = BehaviorSubject.create<Boolean>()

    init {
        this.isVisible.onNext(isVisible)
    }
}
