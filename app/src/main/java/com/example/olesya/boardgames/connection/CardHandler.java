package com.example.olesya.boardgames.connection;

import com.example.olesya.boardgames.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CardHandler implements Runnable {
    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private String clientName;
    private int currentChoice = -1;
    private int totalPts = 0;

    public CardHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            acceptUserEvent();
            server.getScreenCallbacks().initHand(clientName);
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    handleClientMessage(clientMessage);
                    if (clientMessage.equalsIgnoreCase(Utils.CLIENT_CONFIG.END_MSG)) {
                        break;
                    }
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
//            this.close();
        }
    }

    private void acceptUserEvent() {
        String username = inMessage.nextLine();
        this.clientName = server.getAvailableUsername(username, 0);
        if (!clientName.equals(username)) {
            sendMsg(Utils.CLIENT_CONFIG.USERNAME_CHANGED + Utils.DELIM + clientName);
        }

        server.getScreenCallbacks().onAddUserEvent(clientName);
    }

    private void handleClientMessage(String clientMessage) {
        String action = clientMessage.split("#")[0];
        String card;
        switch (action) {
            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_FINISHED:
                card = clientMessage.split(Utils.DELIM)[1];
//                server.getCallbacks().onSelectedCardEvent(new Card(card, false, clientName));
                server.allowUsersToChoose(clientName);
//                server.showMessageFromClient(clientName, action);
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_USER_FINISHED:
                card = clientMessage.split(Utils.DELIM)[1];
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE_FINISHED:
                currentChoice = Integer.valueOf(clientMessage.split(Utils.DELIM)[1]) - 1;
                server.getCallbacks().onAddUserChoice(clientName, currentChoice);
                server.checkForAllUsersChoice();
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP_FINISHED:
                server.getScreenCallbacks().stopRound();
                break;
            case Utils.CLIENT_CONFIG.END_MSG:
                server.getScreenCallbacks().onRemoveUserEvent(clientName);
        }
    }

    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addPts(int pts) {
        totalPts += pts;
    }

    public String getName() {
        return clientName;
    }

    public int getChoice() {
        return currentChoice;
    }

    public int getPts() {
        return totalPts;
    }

    public void resetChoice() {
        currentChoice = -1;
    }
}