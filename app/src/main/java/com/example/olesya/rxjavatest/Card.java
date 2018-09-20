package com.example.olesya.rxjavatest;

public class Card {

    private String img;
    private String playerName;
    private int votes = 0;

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

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
