package com.hdpfans.plugin.model;

import java.util.List;

public class DailyReportModel {

    private String android_id;

    private String bootType;

    private String brand_name;

    private String city;

    private String mac;

    private String os_model;

    private String oskey;

    private String region;

    private String useragent;

    private String version;

    private String other_apps;

    private List<OperateLogModel> dataList;

    public String getAndroidId() {
        return android_id;
    }

    public void setAndroidId(String androidId) {
        this.android_id = androidId;
    }

    public String getBootType() {
        return bootType;
    }

    public void setBootType(String bootType) {
        this.bootType = bootType;
    }

    public String getBrandName() {
        return brand_name;
    }

    public void setBrandName(String brandName) {
        this.brand_name = brandName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOsModel() {
        return os_model;
    }

    public void setOsModel(String osModel) {
        this.os_model = osModel;
    }

    public String getOskey() {
        return oskey;
    }

    public void setOskey(String oskey) {
        this.oskey = oskey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOtherApps() {
        return other_apps;
    }

    public void setOtherApps(String otherApps) {
        this.other_apps = otherApps;
    }

    public List<OperateLogModel> getOperateLog() {
        return this.dataList;
    }

    public void setOperateLog(List<OperateLogModel> operateLog) {
        this.dataList = operateLog;
    }
}
