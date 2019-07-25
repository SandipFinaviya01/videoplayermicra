package com.josieAlan.videoplayermicra.model;

public class Folder {
    private String bucket;
    private String data;
    private String size;
    private String bid;

    public Folder() {
    }

    public Folder(String bucket, String data, String size, String bid) {
        this.bucket = bucket;
        this.data = data;
        this.size = size;
        this.bid = bid;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
}
