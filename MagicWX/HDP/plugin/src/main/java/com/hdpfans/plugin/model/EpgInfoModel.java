package com.hdpfans.plugin.model;

import com.google.gson.annotations.SerializedName;

public class EpgInfoModel {
    private String title;

    private String playtime;

    private String endtime;

    @SerializedName("status")
    private boolean block = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaytime() {
        return playtime;
    }

    public void setPlaytime(String playtime) {
        this.playtime = playtime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
