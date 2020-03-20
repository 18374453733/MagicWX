package com.hdpfans.app.model.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.hdpfans.app.model.annotation.ChannelUpdateType;

@Entity(tableName = "channel_type")
public class ChannelTypeModel {

    @PrimaryKey
    private int id;

    private String name;

    @Ignore
    private int type;

    private boolean hidden;

    private int weigh;

    @Ignore
    public ChannelTypeModel(int id, String name, int weigh) {
        this.id = id;
        this.name = name;
        this.weigh = weigh;
    }

    public ChannelTypeModel() {
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ChannelUpdateType
    public int getType() {
        return type;
    }

    public void setType(@ChannelUpdateType int type) {
        this.type = type;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }
}
