package com.josieAlan.videohdplayerjosie.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Users {

    public String username;
    public double coins;
    public double rupee;
    public String msg;
    public int reqnumber;
    public long userNumber;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Users(String username, double coins, double rupee, String msg, int reqnumber, long userNumber) {
        this.username = username;
        this.coins = coins;
        this.rupee = rupee;
        this.msg = msg;
        this.reqnumber = reqnumber;
        this.userNumber = userNumber;
    }
}
