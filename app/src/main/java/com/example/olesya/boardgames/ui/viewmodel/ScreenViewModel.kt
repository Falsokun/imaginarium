package com.example.olesya.boardgames.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.view.View
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.NonNullMutableLiveData
import com.example.olesya.boardgames.game.controller.GameController
import com.example.olesya.boardgames.ui.dialog.RoundResultDialog

class ScreenViewModel : ViewModel() {

    lateinit var controller: GameController

    var message: MutableLiveData<String> = MutableLiveData()

    var isVisibleStatuses: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)

    fun handleAction(context: Context, action: String?) {
        action ?: return

        when(action) {
            Commands.SCREEN_COMMANDS.SHOW_ROUND_RESULTS ->
                RoundResultDialog(context, controller.players.value, controller::startRound).show()
            Commands.SCREEN_COMMANDS.SHOW_WINNER ->
                RoundResultDialog(context, controller.players.value,
                        controller::startRound).show()
        }
    }
}