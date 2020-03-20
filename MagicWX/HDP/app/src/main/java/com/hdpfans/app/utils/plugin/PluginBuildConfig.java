package com.hdpfans.app.utils.plugin;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 插件信息
 */
public class PluginBuildConfig {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({FromType.ONLINE, FromType.LOCAL})
    public @interface FromType {
        String ONLINE = "online";
        String LOCAL = "LOCAL";
    }

    /**
     * 来源
     */
    private String from;

    /**
     * 编译类型
     */
    private String buildType;

    /**
     * 版本号
     */
    private int versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 渠道
     */
    private String flavor;

    @FromType
    public String getFrom() {
        return from;
    }

    public void setFrom(@FromType String from) {
        this.from = from;
    }

    public String isBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getBuildType() {
        return buildType;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    @Override
    public String toString() {
        return "PluginBuildConfig{" +
                "from='" + from + '\'' +
                ", buildType='" + buildType + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", flavor='" + flavor + '\'' +
                '}';
    }
}
