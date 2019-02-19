package com.example.olesya.boardgames.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.olesya.boardgames.entity.ImaginariumCard
import com.example.olesya.boardgames.entity.Player
import com.example.olesya.boardgames.interfaces.ClientCallback

class CardViewModel: ViewModel(), ClientCallback {

    val player = Player("me")

    val choosing = MutableLiveData<Boolean>()

    val picking = MutableLiveData<Boolean>()

    val message = MutableLiveData<String>()

    var playersNumber: Int = 1

    override fun addCardCallback(card: String) {
        player.addCard(ImaginariumCard(card, true, player.username))
    }

    override fun userPickingEnabled(enabled: Boolean) {
        picking.postValue(enabled)
    }

    override fun userChoosingEnabled(enabled: Boolean, fromNum: Int) {
        playersNumber = fromNum
        choosing.postValue(enabled)
    }

    override fun showMessage(message: String) {
        this.message.postValue(message)
    }

    fun onPicked(position: Int) {
        player.removeCard(position)
        picking.postValue(false)
    }

    override fun usernameChanged(username: String) {
        player.username = username
    }
}