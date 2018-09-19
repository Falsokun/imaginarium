package com.example.olesya.rxjavatest;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends BoundService {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    //для отображения в UI
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int clientNumber = intent.getIntExtra(Utils.CLIENT_NUM, 0);
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
            ClientHandler client = new ClientHandler(serverSocket.getInetAddress().getHostName(),
                    clientSocket, this);
            clients.add(client);
            new Thread(client).start();
        } catch (IOException e) {
        }
    }

    public void showMessageFromClient(String clientName, String clientMessage) {
        message.postValue(clientName + ": " + clientMessage);
    }

    public void onUserAction(String message) {
        new Thread(() -> sendMessageToAllClients(message)).start();
    }

    // отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}