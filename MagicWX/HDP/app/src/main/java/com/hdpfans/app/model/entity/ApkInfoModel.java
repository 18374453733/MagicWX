package com.hdpfans.app.model.entity;

import java.util.List;

/**
 * 配置文件Apk相关信息
 */
public class ApkInfoModel {

    /**
     * apk最新版本
     */
    private int maxVersion;

    /**
     * apk升级文案
     */
    private String updateInfo;

    private String apkUrl;

    /**
     * 强制升级版本
     */
    private List<String> forceVersions;

    private String helpImage;

    private String featureImage;

    private String launchImage;

    public int getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(int maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public List<String> getForceVersions() {
        return forceVersions;
    }

    public void setForceVersions(List<String> forceVersions) {
        this.forceVersions = forceVersions;
    }

    public String getHelpImage() {
        return helpImage;
    }

    public void setHelpImage(String helpImage) {
        this.helpImage = helpImage;
    }

    public String getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

    public String getLaunchImage() {
        return launchImage;
    }

    public void setLaunchImage(String launchImage) {
        this.launchImage = launchImage;
    }
}
