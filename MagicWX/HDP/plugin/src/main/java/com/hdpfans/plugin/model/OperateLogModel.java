package com.hdpfans.plugin.model;

public class OperateLogModel {

    private String id;

    private long start_time;

    private String title;

    private long time;

    private String type;

    private String tv_class;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartTime() {
        return start_time;
    }

    public void setStartTime(long startTime) {
        this.start_time = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTvClass() {
        return tv_class;
    }

    public void setTvClass(String tvClass) {
        this.tv_class = tvClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
