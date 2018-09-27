package com.example.olesya.rxjavatest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class CardHandler implements Runnable {
    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private String clientName;
    private int currentChoice = -1;

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
            initDesk();
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase(Utils.CLIENT_CONFIG.END_MSG)) {
                        break;
                    }

                    handleClientMessage(clientMessage);
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
        String session = inMessage.nextLine();
        String username = inMessage.nextLine();
        this.clientName = server.getAvailableUsername(username, 0);
        if (!clientName.equals(username)) {
            sendMsg(Utils.CLIENT_CONFIG.USERNAME_CHANGED + Utils.DELIM + clientName);
        }

        server.getCallbacks().onAddUserEvent(clientName);
    }

    private void initDesk() {
        ArrayList<String> cards = new ArrayList<>();
        cards.add("0");
        cards.add("1");
        cards.add("2");
        cards.add("3");
        cards.add("4");
        for (String card : cards) {
            server.sendCardToUser(clientName, card);
        }
    }

    private void handleClientMessage(String clientMessage) {
        String action = clientMessage.split("#")[0];
        String card;
        switch (action) {
            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_FINISHED:
                card = clientMessage.split(Utils.DELIM)[1];
                server.getCallbacks().onSelectedCardEvent(new Card(card, clientName));
                server.allowUsersToChoose(clientName);
//                server.showMessageFromClient(clientName, action);
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_USER_FINISHED:
                card = clientMessage.split(Utils.DELIM)[1];
                server.getCallbacks().onUserTurnFinished(new Card(card, clientName));
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE_FINISHED:
                currentChoice = Integer.valueOf(clientMessage.split(Utils.DELIM)[1]);
                server.checkForAllUsersChoice();
                break;
            case Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP_FINISHED:
                server.getCallbacks().uncoverCardsAnimation();
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

    public String getName() {
        return clientName;
    }

    public int getChoice() {
        return currentChoice;
    }
}