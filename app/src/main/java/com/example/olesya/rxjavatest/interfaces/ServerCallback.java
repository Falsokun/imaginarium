package com.example.olesya.rxjavatest.interfaces;

import com.example.olesya.rxjavatest.Card;

public interface ServerCallback {

    void onAddUserEvent(String username);

    void onSelectedCardEvent(Card username);

    void stopRound();

    void onUserTurnFinished(Card card);

    void onShuffleEnd();

    void onAddUserChoice(String clientName, int currentChoice);

    void onStartNewRound();
}
