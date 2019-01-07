package com.example.olesya.boardgames.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.olesya.boardgames.game.controller.GameController

class ScreenViewModel : ViewModel() {

    lateinit var controller: GameController

    var message: MutableLiveData<String> = MutableLiveData()
}