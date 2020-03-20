package com.hdpfans.app.model.entity;

import com.hdpfans.app.model.annotation.RecommendType;

public class Recommend {

    private int type;

    private String name;

    private String tips;

    private String imageUrl;

    private String downloadUrl;

    private String packageName;

    private int channelNum;

    @RecommendType
    public int getType() {
        return type;
    }

    public void setType(@RecommendType int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setIconUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }
}
