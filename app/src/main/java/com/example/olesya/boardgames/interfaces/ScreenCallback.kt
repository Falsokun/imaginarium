package com.example.olesya.boardgames.interfaces

interface ScreenCallback {

    fun onStartNewRound()

    fun onShuffleEnd()

    fun stopRound()

    fun onAddUserEvent(username: String)

    fun onRemoveUserEvent(username: String)

    fun initHand(username: String)
}