package com.example.olesya.boardgames.connection.server

interface ServerCallback {

    fun sendMessageToAll(msg: String)

    fun sendMessageTo(senderId: String, tag: String, msg: String = "")

    fun sendMessageExcept(banId: String, tag: String, msg: String = "")
}