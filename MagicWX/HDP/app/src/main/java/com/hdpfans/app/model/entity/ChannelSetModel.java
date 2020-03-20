package com.hdpfans.app.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelSetModel {

    @SerializedName("type")
    private List<ChannelTypeModel> types;

    @SerializedName("live")
    private List<ChannelModel> channels;

    public List<ChannelTypeModel> getTypes() {
        return types;
    }

    public void setTypes(List<ChannelTypeModel> types) {
        this.types = types;
    }

    public List<ChannelModel> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelModel> channels) {
        this.channels = channels;
    }
}
