package com.example.olesya.rxjavatest.interfaces;

public interface ClientCallback {

    void getCardCallback(String card);

    void onUserTurnEvent();

    void onUserFinishTurnEvent();
}
