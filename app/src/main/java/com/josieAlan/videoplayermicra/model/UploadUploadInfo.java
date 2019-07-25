package com.josieAlan.videoplayermicra.model;

public class UploadUploadInfo {
    public String appName;
    public String value;
    public String appLink;
    public UploadUploadInfo(String name, String full,String appLink) {
        this.appName=name;
        this.value=full;
        this.appLink = appLink;
    }

    public UploadUploadInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }
}