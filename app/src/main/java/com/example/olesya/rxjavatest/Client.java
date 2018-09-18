package com.example.olesya.rxjavatest;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client extends Service {

    private int portNumber = 8888;
    private Socket socketCl = new Socket();
    String host;
    private static final String LOG_TAG = "LOG_TAG";
    private final MyBinder mBinder = new MyBinder();
    private MutableLiveData<String> message = new MutableLiveData<>();
    private PrintWriter serverStream;
    private Scanner inMessage ;

    public Client() {
        socketCl = new Socket();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

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
            // останавливаем выполнение потока на 100 мс
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMessage(MutableLiveData<String> message) {
        this.message = message;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
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
            socketCl.connect((new InetSocketAddress(host, portNumber)), 5000);
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

    private void stopConnection() {
        try {
            socketCl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyBinder extends Binder {
        Client getService() {
            return Client.this;
        }
    }
}
