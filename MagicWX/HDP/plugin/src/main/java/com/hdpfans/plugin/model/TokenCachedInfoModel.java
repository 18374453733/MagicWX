package com.hdpfans.plugin.model;

public class TokenCachedInfoModel {
    private String token;

    private String cid;

    private String loginTime;

    private long time;

    public TokenCachedInfoModel(String token, String cid, String loginTime) {
        this.time = System.currentTimeMillis();
        this.token = token;
        this.cid = cid;
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
