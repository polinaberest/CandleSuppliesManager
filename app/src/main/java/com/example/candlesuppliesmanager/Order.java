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

    public String getUserPhone() {
        return userPhone;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public String getRecieverPhone() {
        return recieverPhone;
    }

    public String getRole() {
        return role;
    }

    public String getCity() {
        return city;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public int getAmountBig() {
        return amountBig;
    }

    public int getAmountMid() {
        return amountMid;
    }

    public int getAmountSmall() {
        return amountSmall;
    }

    public int getTotal() {
        return total;
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
        return "Заявка від " + dateTime + " : " + state + "\n" + "\uD83D\uDD25"  + total + " свічок \nу " + warehouse + ", " + city;
    }

    public String toStringAdmins(){
        return "Заявка від " + dateTime + "\n" + "\uD83D\uDD25" + total + " свічок \nу " + warehouse + ", " + city + "\nотримувач: " + role;
    }

    public String toStringFull(){
        return "Заявка від " + dateTime + "\n" + "\uD83D\uDD25" + "на "  + total + " свічок: \n"
                +((amountBig == 0)?"":(amountBig + " великих\n"))
                +((amountMid == 0)?"":(amountMid + " середніх\n"))
                +((amountSmall == 0)?"":(amountSmall + " маленьких\n"))
                + "\n" + warehouse + ", " + city +
                "\n\nОтримувач: " + role + ", " + recieverName + "\n"
                +recieverPhone;
    }
}

