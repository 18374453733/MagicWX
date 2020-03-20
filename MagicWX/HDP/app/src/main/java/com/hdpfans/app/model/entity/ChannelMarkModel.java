package com.hdpfans.app.model.entity;

/**
 * 配置文件频道角标相关信息
 */
public class ChannelMarkModel {

    /**
     * 频道编号
     */
    private int channelNum;

    /**
     * 频道分类编号
     */
    private Integer typeId;

    /**
     * 角标图片地址
     */
    private String url;

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
