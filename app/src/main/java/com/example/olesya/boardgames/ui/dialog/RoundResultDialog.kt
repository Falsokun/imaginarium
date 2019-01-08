package com.example.olesya.boardgames.ui.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.entity.Player
import kotlinx.android.synthetic.main.dialog_result.*

class RoundResultDialog(context: Context,
                        val players: MutableList<Player>,
                        private val onPositiveButton: () -> Unit) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_result)
        results.text = players.joinToString(separator = "\n") { it.username + ": " + it.score.value }
        button_ok.setOnClickListener {
            onPositiveButton.invoke()
            dismiss()
        }
    }
}