package com.example.olesya.boardgames.interfaces

interface ScreenCallback {

    fun onStartNewRound()

    fun onShuffleEnd()

    fun stopRound()

    fun onAddUserEvent(clientName: String)

    fun onRemoveUserEvent(clientName: String)

    fun initHand(username: String)
}