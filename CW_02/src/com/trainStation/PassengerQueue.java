package com.trainStation;

public class PassengerQueue {
    private static Passenger [] queueArray = new Passenger[42];
    private static int maxLength;
    private static int last;


    public static Passenger[] getQueueArray() {
        return queueArray;
    }

    public void setQueueArray() {
        this.queueArray = queueArray;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public static void addToQueue(Passenger passenger){
        queueArray[last]=passenger;
        last = last + 1;
    }

    public static Passenger remove(int seatNo){
        last = last - 1 ;
        return queueArray[seatNo] = null;
    }

    public static boolean isFull(){
        return false;
    }

    public static void setMaxLength(int maxLength) {
        PassengerQueue.maxLength = maxLength;
    }

    public static int getMaxLength() {
        return maxLength;
    }

}
