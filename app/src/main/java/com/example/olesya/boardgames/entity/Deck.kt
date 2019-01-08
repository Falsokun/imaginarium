package com.example.olesya.boardgames.entity

import android.content.Context
import com.example.olesya.boardgames.database.AppDatabase
import java.util.*

/**
 * Deck - just handles giving out and shuffling cards in the deck
 */
class Deck(context: Context) {

    private var current: MutableList<Card> = mutableListOf()
    private var total: MutableList<Card> = mutableListOf()
    private val random = Random()

    init {
        total.addAll(AppDatabase.getInstance(context)
                .imagesDao()
                .getAllImages()
                .map { Card(it.imageUrl, false) })
    }

    private fun initRandomDeck() {
        current = total.toMutableList()
        Collections.shuffle(current)
    }

    fun getRandomCard(): Card? {
        if (current.size == 0) {
            return null
        }

        val newCard = current[random.nextInt(current.size)]
        current.remove(newCard)
        return newCard
    }

    /**
     * Initializes [current] deck from scratch
     */
    fun reInitDeck() {
        initRandomDeck()
    }
}