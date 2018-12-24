package com.example.olesya.boardgames.interfaces

interface ServerCallback {

    fun sendMessageToAll(msg: String)

    fun sendMessageTo(senderId: String, msg: String)

    fun sendMessageTo(senderId: String, tag: String, msg: String)
}