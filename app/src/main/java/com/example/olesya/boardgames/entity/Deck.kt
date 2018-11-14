package com.example.olesya.boardgames.entity

import android.content.Context
import com.example.olesya.boardgames.database.AppDatabase
import java.util.*
import kotlin.collections.ArrayList

//Val, var - объявление переменных, мне это не нужно, если в конструктор надо передать значения,
//необходимые только (!!!) для инициализации
/**
 * Deck - just handles giving out and shuffling cards in the deck
 */
class Deck(context: Context) {

    var current: ArrayList<Card> = ArrayList()
    var total: ArrayList<Card> = ArrayList()

    init {
        val all = AppDatabase.getInstance(context).imagesDao().getAllImages()
        val random = Random()
        for (img: ImageHolder in all) {
            total.add(Card(img.imageUrl, false))
        }

        val copy = ArrayList<Card>(total)
        while(!copy.isEmpty()) {
            val card = copy[random.nextInt(copy.size)]
            current.add(card)
            copy.remove(card)
        }
    }

    fun getRandomCard(): Card? {
        val r = Random()
        if (current.size == 0) {
            return null
        }

        val newCard = current[r.nextInt(current.size - 1)]
        current.remove(newCard)
        return newCard
    }

    fun cardsLeft(): Int {
        return current.size
    }
}