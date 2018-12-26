package com.example.olesya.boardgames.connection.server

import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.entity.GameController
import com.example.olesya.boardgames.entity.Player
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class CardHandler(socket: Socket, var gameController: GameController) : Runnable {

    private val outMessage = PrintWriter(socket.getOutputStream())
    private val inMessage = BufferedReader(InputStreamReader(socket.getInputStream()))
    val player = Player()

    override fun run() {
        acceptUserEvent()
        var clientMessage: String? = inMessage.readLine()
        while (clientMessage != null) {
            handleClientMessage(clientMessage)
            if (clientMessage.toUpperCase() == Commands.CLIENT_CONFIG.END_MSG) {
                break
            }

            clientMessage = inMessage.readLine()
        }
    }

    /**
     * Accept user and check for username
     */
    private fun acceptUserEvent() {
        val username = inMessage.readLine()
        gameController.onAddUserEvent(username, username)
    }

    private fun handleClientMessage(clientMessage: String) {
        Log.d("Server", clientMessage)
        val action = clientMessage.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        //        String card;
        //        switch (action) {
        //            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_FINISHED:
        //                card = clientMessage.split(Utils.DELIM)[1];
        ////                server.getCallbacks().onSelectedCardEvent(new Card(card, false, clientName));
        //                server.allowUsersToChoose(clientName);
        ////                server.showMessageFromClient(clientName, action);
        //                break;
        //            case Utils.CLIENT_COMMANDS.CLIENT_USER_FINISHED:
        //                card = clientMessage.split(Utils.DELIM)[1];
        //                break;
        //            case Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE_FINISHED:
        //                currentChoice = Integer.valueOf(clientMessage.split(Utils.DELIM)[1]) - 1;
        //                server.getCallbacks().onAddUserChoice(clientName, currentChoice);
        //                server.checkForAllUsersChoice();
        //                break;
        //            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP_FINISHED:
        //                server.getScreenCallbacks().stopRound();
        //                break;
        //            case Utils.CLIENT_CONFIG.END_MSG:
        //                server.getScreenCallbacks().onRemoveUserEvent(clientName);
        //        }
    }

    fun sendMsg(msg: String) {
        try {
            outMessage.println(msg)
            outMessage.flush()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}