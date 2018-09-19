package com.example.olesya.rxjavatest;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends BoundService {

    private Socket socketCl = new Socket();
    String host;
    private PrintWriter serverStream;
    private Scanner inMessage ;

    public Client() {
        socketCl = new Socket();
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
                    || intent.getExtras().getSerializable(Utils.CLIENT_COMMANDS.HOST_CONFIG) == null) {
                message.postValue(Utils.ERR_UNKNOWN);
                //вообще тут бы выходить, но сервис вроде итак работу закончит??
                return;
            }

            host = ((InetAddress) intent.getExtras().getSerializable(Utils.CLIENT_COMMANDS.HOST_CONFIG)).getHostName();
            openConnection();
            sendMessage(Utils.CLIENT_COMMANDS.ENTER_MSG);
            handleEvent();
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleEvent() {
        if (!socketCl.isConnected()) {
            return;
        }

        while (true) {
            // Если от сервера пришло сообщение
            if (inMessage.hasNext()) {
                String serverMsg = inMessage.nextLine();
                // если сервер отправляет данное сообщение, то цикл прерывается и
                // клиент выходит из чата
                if (serverMsg.equalsIgnoreCase(Utils.CLIENT_COMMANDS.END_MSG)) {
                    break;
                }

                // выводим в консоль сообщение (для теста)
//                System.out.println(serverMsg);
                // отправляем данное сообщение всем клиентам
                message.postValue(serverMsg);
                //server.sendMessageToAllClients(clientMessage);
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
//            if (socketCl.isBound() || socketCl.isConnected()) {
//                socketCl.close();
//            }

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
        message.postValue("send");
    }

    public void onUserAction(String message) {
        new Thread(() -> sendMessage(message)).start();
    }

//    private void stopConnection() {
//        try {
//            socketCl.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
