package com.example.olesya.rxjavatest;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.olesya.rxjavatest.ClassModels.BoundService;
import com.example.olesya.rxjavatest.interfaces.ClientCallback;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends BoundService {

    private Socket socketCl;
    String host;
    private PrintWriter serverStream;
    private Scanner inMessage;
    private String clientState = Utils.CLIENT_COMMANDS.CLIENT_WAIT;
    private String username;
    private ClientCallback callback;

    public Client() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            if (intent == null || intent.getExtras() == null
                    || intent.getExtras().getSerializable(Utils.CLIENT_CONFIG.HOST_CONFIG) == null) {
                message.postValue(Utils.ERR_UNKNOWN);
                //вообще тут бы выходить, но сервис вроде итак работу закончит??
                return;
            }

            host = ((InetAddress) intent.getExtras().getSerializable(Utils.CLIENT_CONFIG.HOST_CONFIG)).getHostName();
            username = intent.getExtras().getString(Utils.CLIENT_CONFIG.USERNAME);
            openConnection();
            sendMessage(Utils.CLIENT_CONFIG.ENTER_MSG);
            sendMessage(username);
            handleEvent();
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleEvent() {
        if (!socketCl.isConnected()) {
            return;
        }

        while (true) {
            if (inMessage.hasNext()) {
                String serverMsg = inMessage.nextLine();
                // если сервер отправляет данное сообщение, то цикл прерывается и
                // клиент выходит из чата
                if (serverMsg.equalsIgnoreCase(Utils.CLIENT_CONFIG.END_MSG)) {
                    break;
                }

                handleServerMessage(serverMsg);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void openConnection() {
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            if (socketCl == null || socketCl.isClosed() || socketCl.isConnected()) {
                socketCl = new Socket();
            }

            socketCl.setReuseAddress(true);
            socketCl.bind(null);
            socketCl.connect((new InetSocketAddress(host, PORT_NUMBER)));
            this.serverStream = new PrintWriter(socketCl.getOutputStream(), true);
            this.inMessage = new Scanner(socketCl.getInputStream());
        } catch (ConnectException ex) {
            message.postValue("no screen found, wait until screen starts it work and retry");
        } catch (IOException ex) {
            message.postValue(ex.toString());
            Toast.makeText(this, "io exception", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String str) {
        if (!socketCl.isConnected()) {
            return;
        }

        serverStream.println(str);
    }

    public void setCallbacks(ClientCallback callbacks) {
        callback = callbacks;
    }

    public void onUserAction(String message) {
        new Thread(() -> sendMessage(message)).start();
    }

    private void handleServerMessage(String serverMsg) {
        String action = serverMsg.split(Utils.DELIM)[0];
        switch (action) {
            //ведущий
            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_TURN:
                callback.onMainTurnEvent();
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP:
                callback.onMainStopRoundEvent();
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_USER_TURN:
                callback.onUserTurnEvent();
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE:
                int choice = Integer.valueOf(serverMsg.split(Utils.DELIM)[1]);
                callback.onUserChooseEvent(choice);
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_GET:
                String card = serverMsg.split(Utils.DELIM)[1];
                callback.addCardCallback(card);
                break;
            case Utils.CLIENT_CONFIG.USERNAME_CHANGED:
                username = serverMsg.split(Utils.DELIM)[1];
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socketCl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
