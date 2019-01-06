package com.example.olesya.boardgames.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.olesya.boardgames.entity.ImaginariumCard
import com.example.olesya.boardgames.entity.Player
import com.example.olesya.boardgames.interfaces.ClientCallback

class CardViewModel: ViewModel(), ClientCallback {

    val player = Player()

    val choosing = MutableLiveData<Boolean>()

    val picking = MutableLiveData<Boolean>()

    val message = MutableLiveData<String>()

    val playersNumber: Int = 1

    override fun addCardCallback(card: String) {
        player.addCard(ImaginariumCard(card, true, player.username))
    }

    override fun userPickingEnabled(enabled: Boolean) {
        picking.postValue(enabled)
    }

    override fun userChoosingEnabled(enabled: Boolean) {
        choosing.postValue(enabled)
    }

    override fun showMessage(message: String) {
        this.message.postValue(message)
    }

    fun onPicked(position: Int) {
        player.removeCard(position)
        picking.postValue(false)
    }
}