package com.example.olesya.rxjavatest.interfaces;

public interface ClientCallback {

    //получение карты от сервера
    void getCardCallback(String card);

    //ходит игрок
    void onUserTurnEvent();

    //ходит ведущий
    void onMainTurnEvent();

    //игрок закончил ход
    void onUserFinishTurnEvent(String card);

    //ведущий закончил ход
    void onMainFinishTurnEvent(String card);

    //пользователь выбирает карту со стола
    void onUserChooseEvent();
}
