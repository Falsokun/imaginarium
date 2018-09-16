package com.example.olesya.rxjavatest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class Client extends Service {

    private int portNumber = 8888;
    private Socket socketCl = new Socket();
    String host;
    private static final String LOG_TAG = "LOG_TAG";
    private final IBinder mBinder = new LocalBinder();

    Thread writeThread;

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
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                openConnection();
                sendMessage("any msg");
            }
        });

        writeThread.start();

        return super.onStartCommand(intent, flags, startId);
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
            socketCl.bind(null);
            host = getHostName("def");
            socketCl.connect((new InetSocketAddress(host, portNumber)), 500);
        } catch (ConnectException ex){
            Toast.makeText(this, "no server found", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(this, "io exception", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String str) {
        if (!socketCl.isConnected()) {
            return;
        }

        try {
            OutputStream outputStream = socketCl.getOutputStream();
            outputStream.write(str.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void stopConnection() {
        try {
            socketCl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHostName(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    public class LocalBinder extends Binder {
        Client getService() {
            // Return this instance of LocalService so clients can call public methods
            return Client.this;
        }
    }
}
