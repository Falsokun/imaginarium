package com.example.olesya.boardgames.connection.client

import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.connection.common.BoundService
import com.example.olesya.boardgames.interfaces.ClientCallback
import java.io.IOException
import java.io.PrintWriter
import java.io.Serializable
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class ClientService : BoundService() {

    var host: String = ""
    var username: String = ""
    var socketCl: Socket = Socket()
    lateinit var serverStream: PrintWriter
    lateinit var inMessage: Scanner //TODO: to buffered reader
    var callback: ClientCallback? = null

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("client", "on start client")
        checkConnectionConfig(intent)
        return START_NOT_STICKY
    }

    private fun checkConnectionConfig(intent: Intent?) {
        val inetVar: Serializable? = intent?.extras?.getSerializable(Commands.CLIENT_CONFIG.HOST_CONFIG)
                ?: return

        val inetAddress = inetVar as InetAddress
        Thread {
            host = inetAddress.hostName
        }.start()

        username = intent.extras.getString(Commands.CLIENT_CONFIG.USERNAME)
    }

    fun start() {
        Thread {
            if (host.isEmpty() || username.isEmpty()) {
                callback?.showMessage("host and username are not available")
                return@Thread
            }

            val connectionSuccessful = openConnection()
            if (!connectionSuccessful)
                return@Thread

            sendMessage(username)
            startHandlingEvents()
        }.start()
    }

    private fun startHandlingEvents() {
        if (!socketCl.isConnected) {
            return
        }

        while (true) {
            try {
                val serverMsg = inMessage.nextLine()
                if (serverMsg.equals(Commands.CLIENT_CONFIG.END_MSG, ignoreCase = true)) {
                    screenMessage.postValue(resources.getString(R.string.lost_server))
                    break
                }

                handleServerMessage(serverMsg)
            } catch (ex: NoSuchElementException) {
                ex.printStackTrace()
                screenMessage.postValue(resources.getString(R.string.lost_server))
                break
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
                screenMessage.postValue("Lost server")
                break
            }
        }
    }

    private fun sendMessage(str: String) {
        if (!socketCl.isConnected) {
            return
        }

        try {
            serverStream.println(str)
            serverStream.flush()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun openConnection(): Boolean {
        if (socketCl.isClosed || socketCl.isConnected) {
            socketCl = Socket()
        }

        try {
            socketCl.reuseAddress = true
            socketCl.bind(null)
            socketCl.connect(InetSocketAddress(host, PORT_NUMBER))

            serverStream = PrintWriter(socketCl.getOutputStream())
            inMessage = Scanner(socketCl.getInputStream())
            return true
        } catch (ex: IOException) {
            screenMessage.postValue("problems with socket")
        }

        return false
    }

    fun onUserAction(message: String) {
        Thread { sendMessage(message) }.start()
    }

    override fun onDestroy() {
        stopWithMessage(Commands.CLIENT_CONFIG.END_MSG)
        super.onDestroy()
    }

    private fun stopWithMessage(endMsg: String) {
        Thread {
            sendMessage(endMsg)
            //TODO: check trycatch
            socketCl.close()
            inMessage.close()
            serverStream.close()
        }.start()
    }

    private fun handleServerMessage(serverMsg: String) {
        Log.d("Server", "get from server $serverMsg")
        val action = serverMsg.split(Commands.DELIM)[0]
        callback?.showMessage(action)
        when (action) {
            Commands.CLIENT_COMMANDS.CLIENT_GET -> callback?.addCardCallback(serverMsg.split(Commands.DELIM)[1])
            Commands.CLIENT_COMMANDS.CLIENT_TURN -> callback?.userPickingEnabled(true)
            Commands.CLIENT_COMMANDS.CLIENT_CHOOSE -> callback?.userChoosingEnabled(true, Integer.valueOf(serverMsg.split(Commands.DELIM)[1]))

            Commands.CLIENT_CONFIG.USERNAME -> callback?.usernameChanged(serverMsg.split(Commands.DELIM)[1])
            else -> callback?.showMessage(serverMsg)
        }
    }
}