package com.example.olesya.rxjavatest;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Service {

    private int portNumber = 8888;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private static final String LOG_TAG = "LOG_TAG";
    //для отображения в UI
    private MyBinder binder = new Server.MyBinder();
    private MutableLiveData<String> message = new MutableLiveData<>();
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
            serverSocket = new ServerSocket(portNumber);
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
        finally {
//            try {
                // закрываем подключение
//                clientSocket.close();
//                serverSocket.close();
//            }
//            catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }
    }
//
//    private void readMessage() {
//        try {
//            InputStream inputStream = clientSocket.getInputStream();
//            ByteArrayOutputStream result = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                result.write(buffer, 0, length);
//            }
//
//            message.postValue(result.toString());
//            //            Log.d(LOG_TAG, result.toString());
//        } catch (IOException e) {
//        }
//    }

    public void setMessage(MutableLiveData<String> message) {
        this.message = message;
    }

    public void showMessageFromClient(String clientName, String clientMessage) {
        message.postValue(clientName + ": " + clientMessage);
    }

    class MyBinder extends Binder {
        Server getService() {
            return Server.this;
        }
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