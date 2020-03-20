package com.hdpfans.app.model.entity;

import com.google.gson.annotations.SerializedName;

import hdpfans.com.BuildConfig;

/**
 * 配置文件插件信息
 */
public class PluginModel {

    /**
     * 插件名称
     */
    private String name;

    /**
     * 插件更新时间
     */
    @SerializedName("time")
    private String updateTime;

    /**
     * 插件下载地址
     */
    private String url;

    /**
     * 插件支持版本，默认所有版本
     */
    private String supportVersion;

    /**
     * 插件支持渠道，默认所有渠道
     */
    private String supportFlavor = BuildConfig.FLAVOR;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSupportVersion() {
        return supportVersion;
    }

    public void setSupportVersion(String supportVersion) {
        this.supportVersion = supportVersion;
    }

    public String getSupportFlavor() {
        return supportFlavor;
    }

    public void setSupportFlavor(String supportFlavor) {
        this.supportFlavor = supportFlavor;
    }
}
