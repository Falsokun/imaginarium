package com.example.olesya.rxjavatest;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.olesya.rxjavatest.ClassModels.BoundService;
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
    private int currentPosition = 0;
    ArrayList<Integer> order;
    private int winPts = 100;

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

            int playerNum = intent.getExtras().getInt(Utils.CLIENT_NUM);
            winPts = intent.getExtras().getInt(Utils.WIN_PTS);
            new Thread(() -> {
                for (int i = 0; i < playerNum; i++) { // TODO:!!!!!!!!
                    openConnection();
                }

                sendMessageToAllClients(Utils.CLIENT_COMMANDS.GAME_START);
                choosePlayerOrder();
                setTurnNextUser();
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void setTurnNextUser() {
        CardHandler client = clients.get(currentPosition);
        client.sendMsg(Utils.CLIENT_COMMANDS.CLIENT_MAIN_TURN);
    }

    //тут должна быть какая-то другая логика потом
    private void choosePlayerOrder() {
        order = new ArrayList<>();
        for (int i = 0; i < clients.size(); i++) {
            order.add(i);
        }
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

    public void allowUsersToChoose(String clientName) {
        for (CardHandler o : clients) {
            if (!o.getName().equals(clientName)) {
                o.sendMsg(Utils.CLIENT_COMMANDS.CLIENT_USER_TURN);
            }
        }
    }

    /**
     * этап, когда пользователи выбирают одну карту со стола
     */
    public void startChoosingStep() {
        new Thread(() -> {
            String name = getCurrentMainUser().getName();
            for (CardHandler o : clients) {
                if (!o.getName().equals(name)) {
                    o.sendMsg(Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE + Utils.DELIM + clients.size());
                }
            }
        }).start();
    }

    public String getAvailableUsername(String suggestedUsername, int i) {
        for (CardHandler card : clients) {
            if (suggestedUsername.equals(card.getName())) {
                return getAvailableUsername(suggestedUsername + i, i + 1);
            }
        }

        return suggestedUsername;
    }

    public CardHandler getCurrentMainUser() {
        return clients.get(order.get(currentPosition));
    }

    public void checkForAllUsersChoice() {
        boolean isAllChosen = true;
        String currentMainUser = getCurrentMainUser().getName();
        for (CardHandler user : clients) {
            if (user.getName().equals(currentMainUser))
                continue;

            if (user.getChoice() == -1) {
                isAllChosen = false;
                break;
            }
        }

        if (isAllChosen) {
            getCurrentMainUser().sendMsg(Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP);
        }
    }

    //endregion
}