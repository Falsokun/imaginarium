package com.example.olesya.rxjavatest;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.olesya.rxjavatest.interfaces.ServerCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends BoundService {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<CardHandler> clients = new ArrayList<>();
    private ServerCallback serverCallbacks;

    public Server() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            new Thread(() -> {
                for (int i = 0; i < 1; i++) { // TODO:!!!!!!!!
                    openConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void openConnection() {
        try {
            clientSocket = serverSocket.accept();
            CardHandler client = new CardHandler(clientSocket, this);
            clients.add(client);
            new Thread(client).start();
        } catch (IOException e) {
            message.postValue(e.getMessage());
        }
    }

    //region events
    public void onUserAction(String message) {
        new Thread(() -> sendMessageToAllClients(message)).start();
    }
    //endregion

    //region user communication
    public void showMessageFromClient(String clientName, String clientMessage) {
        message.postValue(clientName + ": " + clientMessage);
    }

    public void sendMessageToAllClients(String msg) {
        for (CardHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void sendCardToUser(String user, String card) {
        CardHandler ch = findUser(user);
        if (ch == null) {
            message.postValue("No user named " + user + "found");
            return;
        }

        ch.sendMsg(Utils.CLIENT_COMMANDS.CLIENT_GET + "#" + card);
    }
    //endregion

    //region event utils
    private CardHandler findUser(String user) {
        for (CardHandler client : clients) {
            if (client.getName().equals(user)) {
                return client;
            }
        }

        return null;
    }

    public void setCallbacks(ServerCallback callbacks) {
        serverCallbacks = callbacks;
    }

    public ServerCallback getCallbacks() {
        return serverCallbacks;
    }
    //endregion
}