package com.example.olesya.boardgames.interfaces

interface ClientCallback {

    //получение карты от сервера
    fun addCardCallback(card: String)

    //выбор карты игроком по тематике
    //ходит игрок
    fun onUserTurnEvent()

    //игрок закончил ход
    fun onUserFinishTurnEvent(card: String)

    //выбор карточки со стола
    fun onUserChooseEvent(playersNum: Int)

    //ходит ведущий
    fun onMainTurnEvent()

    //ведущий закончил ход
    fun onMainFinishTurnEvent(card: String)

    fun onMainStopRoundEvent()
}