package com.example.olesya.boardgames.interfaces

interface ClientCallback {

    //получение карты от сервера
    fun addCardCallback(card: String)

    fun userPickingEnabled(enabled: Boolean)

    fun userChoosingEnabled(enabled: Boolean)

    fun showMessage(message: String)
}