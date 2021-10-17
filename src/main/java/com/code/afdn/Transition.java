package com.code.afdn;

public class Transition {
    private int from;
    private int to;
    private String read;

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public String getRead() {
        return read;
    }

    public Transition(int from, int to, String read) {
        this.from = from;
        this.to = to;
        this.read = read;
    }

    @Override
    public String toString() {
        return "Transition [\n\t\tfrom=" + from + ", \n\t\tread=" + read + ", \n\t\tto=" + to + "\n\t]";
    }
}