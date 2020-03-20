package com.hdpfans.app.model.entity;

import java.util.List;

/**
 * 节目频道屏蔽信息
 */
public class BlockInfoModel {

    /**
     * 频道编号
     */
    private int channelNum;

    /**
     * 屏蔽区域，为null则全区域屏蔽
     */
    private List<String> regions;

    /**
     * 屏蔽时间段
     */
    private List<BlockTimesModel> btimes;

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<BlockTimesModel> getBtimes() {
        return btimes;
    }

    public void setBtimes(List<BlockTimesModel> btimes) {
        this.btimes = btimes;
    }
}
