package com.hdpfans.app.model.entity;

import hdpfans.com.R;

import com.hdpfans.app.model.annotation.QualityType;

public enum QualityEnum {

//    SD(QualityType.SD, "标清", R.color.quality_SD),
    HD(QualityType.HD, "高清", R.color.quality_HD),
    SHD(QualityType.SHD, "超清", R.color.quality_SHD);

    private String type;

    private String name;

    private int colorId;

    QualityEnum(String type, String name, int colorId) {
        this.type = type;
        this.name = name;
        this.colorId = colorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }
}
