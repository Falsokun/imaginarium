package com.example.olesya.boardgames.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.olesya.boardgames.Entity.Card
import com.example.olesya.boardgames.Entity.GameController
import com.example.olesya.boardgames.Entity.ImaginariumCard
import com.example.olesya.boardgames.interfaces.ItemCallback

class ScreenViewModel : ViewModel(), ItemCallback {

    lateinit var controller: GameController

    var message: MutableLiveData<String> = MutableLiveData()

    /**
     * Selected card means that player chose card to show on table
     */
    override fun onSelectedCardEvent(username: Card) {
        controller.addCard(username as ImaginariumCard)
    }

    /**
     * User chose card due to association
     */
    override fun onUserTurnFinished(card: Card) {
        controller.addCard(card as ImaginariumCard)
        if (controller.isRoundOver())
            controller.shuffleCards()
    }

    override fun onAddUserChoice(clientName: String, currentChoice: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}