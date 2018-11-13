package com.example.olesya.boardgames.interfaces;

import com.example.olesya.boardgames.Entity.Card;

public interface ItemCallback {

    void onSelectedCardEvent(Card username);

    void onUserTurnFinished(Card card);

    void onAddUserChoice(String clientName, int currentChoice);
}
