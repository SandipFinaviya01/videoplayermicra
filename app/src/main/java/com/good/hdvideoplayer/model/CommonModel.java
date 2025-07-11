package com.good.hdvideoplayer.model;

public class CommonModel {
    public double appvideo;
    public double conversion;
    public double reward;
    public int showreward;
    public double minwithdraw;
    public long userNumber;
    public int showwallet;

    public CommonModel() {
    }
    public CommonModel(double appvideo, double conversion, double reward, int showreward, double minwithdraw, long userNumber, int showwallet) {
        this.appvideo = appvideo;
        this.conversion = conversion;
        this.reward = reward;
        this.showreward = showreward;
        this.minwithdraw = minwithdraw;
        this.userNumber = userNumber;
        this.showwallet = showwallet;
    }
}
