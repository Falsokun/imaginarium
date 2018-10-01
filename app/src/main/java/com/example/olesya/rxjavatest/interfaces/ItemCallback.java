package com.example.olesya.rxjavatest.interfaces;

import com.example.olesya.rxjavatest.Card;

public interface ItemCallback {

    void onSelectedCardEvent(Card username);

    void onUserTurnFinished(Card card);

    void onAddUserChoice(String clientName, int currentChoice);
}
