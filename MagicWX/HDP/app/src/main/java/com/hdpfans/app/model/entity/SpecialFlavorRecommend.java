package com.hdpfans.app.model.entity;

import java.util.List;

public class SpecialFlavorRecommend {

    private List<String> flavorName;

    private List<Recommend> recommend;

    public List<String> getFlavorName() {
        return flavorName;
    }

    public void setFlavorName(List<String> flavorName) {
        this.flavorName = flavorName;
    }

    public List<Recommend> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<Recommend> recommend) {
        this.recommend = recommend;
    }
}
