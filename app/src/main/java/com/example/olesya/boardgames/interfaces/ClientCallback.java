package com.example.olesya.boardgames.interfaces;

public interface ClientCallback {

    //получение карты от сервера
    void addCardCallback(String card);

    //выбор карты игроком по тематике
    //ходит игрок
    void onUserTurnEvent();

    //игрок закончил ход
    void onUserFinishTurnEvent(String card);

    //выбор карточки со стола
    void onUserChooseEvent(int playersNum);

    //ходит ведущий
    void onMainTurnEvent();

    //ведущий закончил ход
    void onMainFinishTurnEvent(String card);

    void onMainStopRoundEvent();

}
