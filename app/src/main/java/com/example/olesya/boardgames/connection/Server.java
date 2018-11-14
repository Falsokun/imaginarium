package com.example.olesya.boardgames.connection;

import android.content.Intent;

import com.example.olesya.boardgames.Utils;
import com.example.olesya.boardgames.adapter.CardPagerAdapter;
import com.example.olesya.boardgames.interfaces.ItemCallback;
import com.example.olesya.boardgames.interfaces.ScreenCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends BoundService {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<CardHandler> clients = new ArrayList<>();
    private ItemCallback itemCallbacks;
    private ScreenCallback screenCallbacks;
    private int currentPosition = 0;
    ArrayList<Integer> order;
    private int winPts = 100;
    private int totalPlayerNum = 1;

    public Server() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            if (intent.getExtras() != null) {
                totalPlayerNum = intent.getExtras().getInt(Utils.CLIENT_NUM);
                winPts = intent.getExtras().getInt(Utils.WIN_PTS);
                new Thread(() -> {
                    for (int i = 0; i < totalPlayerNum; i++) {
                        openConnection();
                    }

                    sendMessageToAllClients(Utils.CLIENT_COMMANDS.GAME_START);
                    choosePlayerOrder();
                    setTurnNextUser();
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void setTurnNextUser() {
        if (clients == null || clients.size() <= currentPosition)
            return;

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
            serviceMessage.postValue(e.getMessage());
        }
    }

    //region user communication
    public void sendMessageToAllClients(String msg) {
        for (CardHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void sendCardToUser(String user, String card) {
        CardHandler ch = findUser(user);
        if (ch == null) {
            serviceMessage.postValue("No user named " + user + "found");
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

    public void setCallbacks(ItemCallback callbacks) {
        itemCallbacks = callbacks;
    }

    public void setScreenCallbacks(ScreenCallback callbacks) {
        screenCallbacks = callbacks;
    }

    public ItemCallback getCallbacks() {
        return itemCallbacks;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serverSocket != null && !serverSocket.isClosed()) {
            for (CardHandler client : clients) {
                client.sendMsg(Utils.CLIENT_CONFIG.END_MSG);
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //пока не расписано, что все отгадали
    public void countRoundPts(CardPagerAdapter cardAdapter) {
//        String mainUserName = getCurrentMainUser().getName();
//        for (int i = 0; i < cardAdapter.getData().size(); i++) {
//            Imagin curCard = cardAdapter.getData().get(i);
//            ArrayList<String> curVotes = cardAdapter.getVotesByNum(i);
//            if (curCard.getPlayerName().equals(mainUserName)) {
//                if (cardAdapter.getVotesByNum(i).size() != 0) {
//                    getCurrentMainUser().addPts(Utils.GUESSED_PTS);
//                    addToUsersPts(curVotes);
//                } else { //никто не угадал
//                    getCurrentMainUser().addPts(Utils.NO_ONE_GUESSED_PTS);
//                }
//            } else {
//                findPlayerByName(curCard.getPlayerName()).addPts(curVotes.size());
//            }
//        }
    }


    private void addToUsersPts(ArrayList<String> players) {
        for (String player : players) {
            CardHandler cardHandler = findPlayerByName(player);
            if (cardHandler != null) {
                cardHandler.addPts(Utils.GUESSED_PTS);
            }
        }
    }

    private CardHandler findPlayerByName(String player) {
        for (CardHandler cardHandler : clients) {
            if (cardHandler.getName().equals(player))
                return cardHandler;
        }

        return null;
    }

    public void changeLeader() {
        currentPosition++;
        for (CardHandler client : clients) {
            client.resetChoice();
        }
    }

    public boolean hasClientWinPts() {
        for (CardHandler client : clients) {
            if (client.getPts() >= winPts) {
                return true;
            }
        }

        return false;
    }

    public String getStringResults() {
        StringBuilder str = new StringBuilder();
        String delim = ": ";
        for (CardHandler client : clients) {
            str.append(client.getName());
            str.append(delim);
            str.append(client.getPts());
            str.append("\n");
        }

        return str.toString();
    }

    public ScreenCallback getScreenCallbacks() {
        return screenCallbacks;
    }

    public void removePlayer(String clientName) {
        clients.remove(findPlayerByName(clientName));
//        if (clients.size() < totalPlayerNum) {
//            openConnection();
//            isGameStopped = clients.size() == totalPlayerNum;
//        }
    }

    public void stopGame() {
        sendMessageToAllClients(Utils.CLIENT_COMMANDS.GAME_STOP);
    }
    //endregion
}