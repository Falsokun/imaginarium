package com.example.olesya.boardgames.connection.server

import android.content.Intent
import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.connection.common.BoundService
import com.example.olesya.boardgames.entity.GameController
import com.example.olesya.boardgames.entity.Player
import java.net.ServerSocket
import java.net.SocketException

/**
 * Отвечает только за подключение пользователей и передачу сообщений
 */
class Server : BoundService(), ServerCallback {

    lateinit var gameController: GameController

    private var serverSocket: ServerSocket = ServerSocket(PORT_NUMBER)

    private val clients: MutableList<CardHandler> = mutableListOf()

    private lateinit var connectionController: Thread

    /**
     * Initializes searching game: opens server socket, waits for all users, sets the turn of user
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras == null)
            return START_NOT_STICKY

        val totalPlayerNum = intent.extras.getInt(Commands.CLIENT_NUM)
        val winPts = intent.extras.getInt(Commands.WIN_PTS)
        initController(totalPlayerNum, winPts)
        return START_NOT_STICKY
    }

    /**
     * Подключает всех пользователей и запускает игру
     */
    private fun initController(totalPlayerNum: Int, winPts: Int) {
        gameController = GameController(this, mutableListOf(), winPts, this)

        connectionController = Thread(Runnable {
            val players = mutableListOf<Player>()
            try {
//                for (i in 0..totalPlayerNum) {
                    openConnection()?.let { players.add(it) }
//                }

                gameController.players = players
                sendMessageToAll(Commands.CLIENT_COMMANDS.GAME_START)
                gameController.startGame()
            } catch (ex: SocketException) {
                Log.d("server", "closed")
            }
        })

        connectionController.start()
    }

    /**
     * Открывает соединение с клиентом
     */
    private fun openConnection(): Player? {
        if (serverSocket.isClosed)
            return null
//            serverSocket = ServerSocket(PORT_NUMBER)

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
        sendMessageTo(senderId, tag + Commands.DELIM + msg)
    }

    override fun onDestroy() {
        connectionController.interrupt()
        serverSocket.close()
        super.onDestroy()
    }
}