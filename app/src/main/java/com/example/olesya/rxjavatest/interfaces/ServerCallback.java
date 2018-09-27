package com.example.olesya.rxjavatest.interfaces;

import com.example.olesya.rxjavatest.Card;

public interface ServerCallback {

    void onAddUserEvent(String username);

    void onSelectedCardEvent(Card username);

    void uncoverCardsAnimation();

    void onUserTurnFinished(Card card);
}
