package com.example.olesya.rxjavatest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

//import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Service {

    private int portNumber = 8888;
    private BufferedReader br;
    private ServerSocket serverSocket;
    private Socket client;
    private static final String LOG_TAG = "LOG_TAG";
    Thread readThread;
    //для отображения в UI
    private Context context;

    public Server() {
    }

    public Server(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        readThread = new Thread(() -> {
            openConnection();
            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            readMessage();
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    private void openConnection() {
        try {
            serverSocket = new ServerSocket(portNumber);
            client = serverSocket.accept();
        } catch (IOException e) {
        }
    }

    private void readMessage() {
        try {
            InputStream inputStream = client.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

//            Log.d(LOG_TAG, result.toString());
            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    private void stopConnection() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}