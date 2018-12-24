package com.example.olesya.boardgames.connection

import android.content.Intent
import android.util.Log
import com.example.olesya.boardgames.Utils
import com.example.olesya.boardgames.entity.GameController
import com.example.olesya.boardgames.entity.Player
import com.example.olesya.boardgames.interfaces.ServerCallback
import java.net.ServerSocket

/**
 * Отвечает только за подключение пользователей и передачу сообщений
 */
class Server : BoundService(), ServerCallback {

    lateinit var gameController: GameController

    private val serverSocket: ServerSocket = ServerSocket(PORT_NUMBER)

    private val clients: MutableList<CardHandler> = mutableListOf()

    /**
     * Initializes searching game: opens server socket, waits for all users, sets the turn of user
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras == null)
            return super.onStartCommand(intent, flags, startId)

        val totalPlayerNum = intent.extras.getInt(Utils.CLIENT_NUM)
        val winPts = intent.extras.getInt(Utils.WIN_PTS)
        initController(totalPlayerNum, winPts)
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Подключает всех пользователей и запускает игру
     */
    private fun initController(totalPlayerNum: Int, winPts: Int) {
        gameController = GameController(this, mutableListOf(), winPts, this)

        Thread {
            val players = mutableListOf<Player>()
//            for (i in 0..totalPlayerNum) {
            openConnection(serverSocket)?.let { players.add(it) }
//            }

            gameController.players = players
            sendMessageToAll(Utils.CLIENT_COMMANDS.GAME_START)
            gameController.startGame()
        }.start()
    }

    /**
     * Открывает соединение с клиентом
     */
    private fun openConnection(serverSocket: ServerSocket): Player? {
        val clientSocket = serverSocket.accept()
        val client = CardHandler(clientSocket, gameController)
        clients.add(client)
        Thread(client).start()
        return client.player
    }

    /**
     * Отправляет сообщение всем подключенным пользователям
     */
    override fun sendMessageToAll(msg: String) {
        Log.d("Server", "send to all $msg")
        for (client in clients) {
            client.sendMsg(msg)
        }
    }

    /**
     * Отправляет сообщение пользователю
     */
    override fun sendMessageTo(senderId: String, msg: String) {
        val client = clients.find { x -> x.player.username == senderId }
        Log.d("Server", "send to $senderId - $msg")
        client?.sendMsg(msg)
    }

    /**
     * Отправляет сообщение пользователю
     */
    override fun sendMessageTo(senderId: String, tag: String, msg: String) {
        sendMessageTo(senderId, tag + Utils.DELIM + msg)
    }
}