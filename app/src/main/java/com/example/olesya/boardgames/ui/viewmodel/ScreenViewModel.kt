package com.example.olesya.boardgames.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.example.olesya.boardgames.entity.Card
import com.example.olesya.boardgames.entity.GameController
import com.example.olesya.boardgames.entity.ImaginariumCard
import com.example.olesya.boardgames.entity.Player
import com.example.olesya.boardgames.interfaces.ItemCallback

class ScreenViewModel : ViewModel(), ItemCallback {

    lateinit var controller: GameController
    var message: MutableLiveData<String> = MutableLiveData()

    fun addPlayers(context: Context, players: ArrayList<Player>) {
        controller = GameController(context, players)
    }
    /**
     * Selected card means that player chose card to show on table
     */
    override fun onSelectedCardEvent(card: Card) {
        controller.addCard(card as ImaginariumCard)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}