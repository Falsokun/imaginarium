package com.example.olesya.rxjavatest.interfaces;

public interface ScreenCallback {

    void onStartNewRound();

    void onShuffleEnd();

    void stopRound();

    void onAddUserEvent(String clientName);

    void onRemoveUserEvent(String clientName);

    void initDesk(String username);
}
