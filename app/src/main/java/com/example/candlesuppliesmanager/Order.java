package com.example.candlesuppliesmanager;

import android.graphics.Color;

import androidx.annotation.NonNull;

public class Order implements Comparable<Order> {
    private String requestID, userPhone, recieverName, recieverPhone, role, city, warehouse, dateTime, state;
    private int amountBig, amountMid, amountSmall, total;
    private boolean isSeen, isApproved;

    public Order(String requestID, String userPhone, String recieverName, String recieverPhone, String role, String city, String warehouse, String dateTime, int amountBig, int amountMid, int amountSmall, boolean isSeen, boolean isApproved) {
        this.requestID = requestID;
        this.userPhone = userPhone;
        this.recieverName = recieverName;
        this.recieverPhone = recieverPhone;
        this.role = role;
        this.city = city;
        this.warehouse = warehouse;
        this.dateTime = dateTime;
        this.amountBig = amountBig;
        this.amountMid = amountMid;
        this.amountSmall = amountSmall;
        total = amountBig + amountMid + amountSmall;
        this.isSeen = isSeen;
        this.isApproved = isApproved;
        setState();
    }

    public boolean isSeen() {
        return isSeen;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public String getRequestID() {
        return requestID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getState() {
        return state;
    }

    public void setState()
    {
        state = "не переглянута";
        if(isSeen){
            state = isApproved ? "схвалена" : "відхилена";
        }
    }
    @Override
    public int compareTo(Order o) {
        return dateTime.compareTo(o.getDateTime());
    }

    @NonNull
    @Override
    public String toString() {
        return "Заявка від " + dateTime + " : " + state + "\n"  + total + " свічок \nу " + warehouse + ", " + city;
    }
}

