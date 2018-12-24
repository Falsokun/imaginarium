package com.example.olesya.boardgames.interfaces

import com.example.olesya.boardgames.entity.Player


interface ScreenCallback {

    fun onStartNewRound()

    fun onShuffleEnd()

    fun stopRound()

    fun onAddUserEvent(username: String, renamedFrom: String): Player

    fun onRemoveUserEvent(username: String)

    fun initHand(username: String)
}