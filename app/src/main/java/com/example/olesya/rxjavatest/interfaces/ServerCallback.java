package com.example.olesya.rxjavatest.interfaces;

public interface ServerCallback {

    void onAddUserEvent(String username);

    void onSelectedCardEvent(String username);
}
