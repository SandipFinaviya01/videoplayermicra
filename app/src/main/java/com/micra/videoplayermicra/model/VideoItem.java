package com.micra.videoplayermicra.model;

import java.io.Serializable;

public class VideoItem implements Serializable {
    private String _ID;
    private String SIZE;
    private String DATE;
    public String DATA;
    public String DISPLAY_NAME;
    private String DURATION;
    private String Bucket;


    public VideoItem(String _ID, String SIZE, String TITLE,
                     String DATA, String DISPLAY_NAME, String DURATION, String bucket) {

        this._ID = _ID;
        this.SIZE = SIZE;
        this.DATE = TITLE;
        this.DATA = DATA;
        this.DISPLAY_NAME = DISPLAY_NAME;
        this.DURATION = DURATION;
        this.Bucket = bucket;
    }

    public VideoItem() {

    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getSIZE() {
        return SIZE;
    }

    public void setSIZE(String SIZE) {
        this.SIZE = SIZE;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    public String getDURATION() {
        return DURATION;
    }

    public void setDURATION(String DURATION) {
        this.DURATION = DURATION;
    }

    public String getBucket() {
        return Bucket;
    }

    public void setBucket(String bucket) {
        Bucket = bucket;
    }
}
