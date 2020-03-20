package com.hdpfans.app.model.entity;

import java.util.List;

public class BlockVersionChannel {

    private List<Integer> channelNums;

    private List<String> streamPregs;

    public List<Integer> getChannelNums() {
        return channelNums;
    }

    public void setChannelNums(List<Integer> channelNums) {
        this.channelNums = channelNums;
    }

    public List<String> getStreamPregs() {
        return streamPregs;
    }

    public void setStreamPregs(List<String> streamPregs) {
        this.streamPregs = streamPregs;
    }
}
