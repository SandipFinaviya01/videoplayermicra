package com.josieAlan.videohdplayerjosie.model;

public class RequestModel {
    public String mobileNo;
    public double money;
    public String status;
    public int requestNo;

    public RequestModel() {
    }

    public RequestModel(String mobileNo, double money, String status, int requestNo) {
        this.mobileNo = mobileNo;
        this.money = money;
        this.status = status;
        this.requestNo = requestNo;
    }
}
