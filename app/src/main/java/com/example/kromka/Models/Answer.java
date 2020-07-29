package com.example.kromka.Models;

public class Answer {
    private String text;
    private int points;
    private boolean isChosen = false;

    public Answer(String text, int points) {
        this.text = text;
        this.points = points;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
