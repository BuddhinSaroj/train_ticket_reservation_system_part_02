package com.trainStation;

public class Passenger {
    private  String name;
    private  int secondsInQueue;
    private int seat;
    private int totalSeconds;

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public  String getName() {
        return name;
    }

    public void setName(String firstName,String surName) {
        this.name = firstName+" "+surName;
    }

    public  int getSecondsInQueue() {
        return secondsInQueue;
    }

    public void setSecondsInQueue(int secondsInQueue) {
        this.secondsInQueue = secondsInQueue;
    }
}
