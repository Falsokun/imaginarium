package com.example.olesya.boardgames.interfaces;

public interface ScreenCallback {

    void onStartNewRound();

    void onShuffleEnd();

    void stopRound();

    void onAddUserEvent(String clientName);

    void onRemoveUserEvent(String clientName);

    void initHand(String username);
}
