package com.hdpfans.plugin.model;

public class UrlCachedModel {

    private long time;

    private String url;

    public UrlCachedModel(String url) {
        this.time = System.currentTimeMillis();
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
