package com.example.olesya.boardgames.connection

import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.olesya.boardgames.Utils
import com.example.olesya.boardgames.interfaces.ClientCallback
import java.io.PrintWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class ClientService : BoundService() {

    var host: String = ""
    var username: String = ""
    var socketCl: Socket = Socket()
    lateinit var serverStream: PrintWriter
    lateinit var inMessage: Scanner
    var callback: ClientCallback? = null

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkConnectionConfig(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkConnectionConfig(intent: Intent?) {
        val inetAddress = intent?.extras?.getSerializable(Utils.CLIENT_CONFIG.HOST_CONFIG) as InetAddress
        host = inetAddress.hostName
        username = intent.extras.getString(Utils.CLIENT_CONFIG.USERNAME)
    }

    fun start() {
        Thread {
            if (host.isEmpty() || username.isEmpty())
                return@Thread

            openConnection()
            sendMessage(username)
            startHandlingEvents()
        }.start()
    }

    fun startHandlingEvents() {
        if (!socketCl.isConnected) {
            return
        }

//        while (true) {
//            if (inMessage.hasNext()) {
//                val serverMsg = inMessage.nextLine()
//                // если сервер отправляет данное сообщение, то цикл прерывается и
//                // клиент выходит из чата
//                if (serverMsg.equals(Utils.CLIENT_CONFIG.END_MSG, ignoreCase = true)) {
//                    serviceMessage.postValue(resources.getString(R.string.lost_server))
//                    break
//                }
//
//                handleServerMessage(serverMsg)
//            }
//            try {
//                Thread.sleep(100)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//
//        }
    }

    private fun sendMessage(str: String) {
        if (!socketCl.isConnected) {
            return
        }

        serverStream.println(str)
    }

    private fun openConnection() {
        if (socketCl.isClosed || socketCl.isConnected) {
            socketCl = Socket()
        }

        socketCl.reuseAddress = true
        socketCl.bind(null)
        socketCl.connect(InetSocketAddress(host, PORT_NUMBER))

        serverStream = PrintWriter(socketCl.getOutputStream())
        inMessage = Scanner(socketCl.getInputStream())
    }

    fun onUserAction(message: String) {
        Thread { sendMessage(message) }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWithMessage(Utils.CLIENT_CONFIG.END_MSG)
    }

    private fun stopWithMessage(endMsg: String) {
        Thread {
            sendMessage(endMsg)
            socketCl.close()
        }.start()
    }

    private fun handleServerMessage(serverMsg: String) {
        Log.d("Server", "get from server $serverMsg")
        val action = serverMsg.split(Utils.DELIM.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]
        when (action) {
            //ведущий
            Utils.CLIENT_COMMANDS.CLIENT_MAIN_TURN -> callback?.onMainTurnEvent()
            Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP -> callback?.onMainStopRoundEvent()
            Utils.CLIENT_COMMANDS.CLIENT_USER_TURN -> callback?.onUserTurnEvent()
            Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE -> {
                val choice = Integer.valueOf(serverMsg.split(Utils.DELIM.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1])
                callback?.onUserChooseEvent(choice)
            }
            Utils.CLIENT_COMMANDS.CLIENT_GET -> {
                val card = serverMsg.split(Utils.DELIM.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
                callback?.addCardCallback(card)
            }
            Utils.CLIENT_CONFIG.USERNAME_CHANGED -> username = serverMsg.split(Utils.DELIM.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
        }
    }
}