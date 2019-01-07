package com.example.olesya.boardgames.connection.server

import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.Commands.DELIM
import com.example.olesya.boardgames.game.controller.GameController
import com.example.olesya.boardgames.entity.Player
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class CardHandler(socket: Socket, private var gameController: GameController) : Runnable {

    private val outMessage = PrintWriter(socket.getOutputStream())
    private val inMessage = BufferedReader(InputStreamReader(socket.getInputStream()))
    var username: String = ""

    override fun run() {
        acceptUserEvent()
        var clientMessage: String? = inMessage.readLine()
        while (clientMessage != null) {
            handleClientMessage(clientMessage)
            if (clientMessage.toUpperCase() == Commands.CLIENT_CONFIG.END_MSG) {
                gameController.onRemovePlayer(username)
            }

            clientMessage = inMessage.readLine()
        }
    }

    /**
     * Accept user and check for username
     */
    private fun acceptUserEvent() {
        username = inMessage.readLine()
        gameController.onAddUserEvent(username)
    }

    private fun handleClientMessage(clientMessage: String) {
        Log.d("Server", clientMessage)
        val action = clientMessage.split(DELIM)[0]
        gameController.screenMessage.postValue(action)
        when (action) {
            Commands.CLIENT_COMMANDS.CLIENT_TURN ->
                gameController.clientPicksCard(username, clientMessage.split(DELIM)[1])
            Commands.CLIENT_COMMANDS.CLIENT_CHOOSE ->
                gameController.clientChoosesCard(username, clientMessage.split(DELIM)[1])
        }
    }

    fun sendMsg(msg: String) {
        Thread {
            try {
                outMessage.println(msg)
                outMessage.flush()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }
}