package com.example.olesya.boardgames;

import java.util.ArrayList;

public class Card {

    private String img;
    private String playerName;
    private ArrayList<Integer> votes = new ArrayList<>();

    public Card(String img) {
        this.img = img;
    }

    public Card(String img, String playerName) {
        this.img = img;
        this.playerName = playerName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void addVote(int vote) {
        votes.add(vote);
    }

    public ArrayList<Integer> getVotes() {
        return votes;
    }
}
