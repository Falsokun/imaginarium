package com.example.olesya.rxjavatest.interfaces;

import com.example.olesya.rxjavatest.Card;

public interface ServerCallback {

    void onAddUserEvent(String username);

    void onSelectedCardEvent(Card username);

    //проверяет все ли пользователи положили карты на стол
    void checkForAllCardsOnDesk();
}
