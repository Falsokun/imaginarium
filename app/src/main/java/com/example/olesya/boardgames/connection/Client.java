package com.example.olesya.boardgames.connection;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.olesya.boardgames.R;
import com.example.olesya.boardgames.Utils;
import com.example.olesya.boardgames.interfaces.ClientCallback;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends BoundService {

    private Socket socketCl;
    private String host;
    private PrintWriter serverStream;
    private Scanner inMessage;
    private String username;
    private ClientCallback callback;
    private Intent intent;

    public Client() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    public void start() {
        new Thread(() -> {
            if (!checkConnectionConfig(intent)) {
                Toast.makeText(this, "что-то с intent", Toast.LENGTH_SHORT).show();
                return;
            }

            openConnection();
            sendMessage(username);
            startHandlingEvents();
        }).start();
    }

    public boolean checkConnectionConfig(Intent intent) {
        InetAddress inetAddress;
        if (intent == null || intent.getExtras() == null
                || intent.getExtras().getSerializable(Utils.CLIENT_CONFIG.HOST_CONFIG) == null) {
            serviceMessage.postValue(getResources().getString(R.string.err_no_p2p_conn));
            return false;
        }

        inetAddress = (InetAddress) intent.getExtras().getSerializable(Utils.CLIENT_CONFIG.HOST_CONFIG);
        if (inetAddress == null) {
            return false;
        }

        host = inetAddress.getHostName();
        username = intent.getExtras().getString(Utils.CLIENT_CONFIG.USERNAME);
        return true;
    }

    public void startHandlingEvents() {
        if (!socketCl.isConnected()) {
            return;
        }

        while (true) {
            if (inMessage.hasNext()) {
                String serverMsg = inMessage.nextLine();
                // если сервер отправляет данное сообщение, то цикл прерывается и
                // клиент выходит из чата
                if (serverMsg.equalsIgnoreCase(Utils.CLIENT_CONFIG.END_MSG)) {
                    serviceMessage.postValue(getResources().getString(R.string.lost_server));
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
            serviceMessage.postValue("no screen found, wait until screen starts it work and retry");
        } catch (IOException ex) {
            serviceMessage.postValue(ex.toString());
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
        if (socketCl != null) {
            stopWithMessage(Utils.CLIENT_CONFIG.END_MSG);
        }
    }

    private void stopWithMessage(String endMsg) {
        new Thread(() -> {
            try {
                sendMessage(endMsg);
                socketCl.close();
            } catch (IOException e) {
                serviceMessage.postValue(e.toString());
            }
        }).start();
    }
}
