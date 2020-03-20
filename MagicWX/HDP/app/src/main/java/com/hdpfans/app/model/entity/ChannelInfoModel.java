package com.hdpfans.app.model.entity;

import java.util.List;

/**
 * 配置文件频道相关信息
 */
public class ChannelInfoModel {

    /**
     * 节目源下载地址
     */
    private String url;

    /**
     * 节目源当前版本
     */
    private int maxVersion;

    /**
     * 默认开机频道
     */
    private int bootChannel;

    /**
     * 屏蔽节目源信息
     */
    private List<BlockInfoModel> block;

    /**
     * 需要显示区域的频道号
     */
    private List<Integer> showAreaNums;

    /**
     * 过滤版权的节目源
     */
    private BlockVersionChannel blockVersionChannel;

    /**
     * 增量更新下载地址
     */
    private String incrementBaseUrl;

    /**
     * 省内频道插入坐标
     */
    private int withinRegionIndex;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(int maxVersion) {
        this.maxVersion = maxVersion;
    }

    public int getBootChannel() {
        return bootChannel;
    }

    public void setBootChannel(int bootChannel) {
        this.bootChannel = bootChannel;
    }

    public List<BlockInfoModel> getBlock() {
        return block;
    }

    public void setBlock(List<BlockInfoModel> block) {
        this.block = block;
    }

    public BlockVersionChannel getBlockVersionChannel() {
        return blockVersionChannel;
    }

    public void setBlockVersionChannel(BlockVersionChannel blockVersionChannel) {
        this.blockVersionChannel = blockVersionChannel;
    }

    public String getIncrementBaseUrl() {
        return incrementBaseUrl;
    }

    public void setIncrementBaseUrl(String incrementBaseUrl) {
        this.incrementBaseUrl = incrementBaseUrl;
    }

    public int getWithinRegionIndex() {
        return withinRegionIndex;
    }

    public void setWithinRegionIndex(int withinRegionIndex) {
        this.withinRegionIndex = withinRegionIndex;
    }

    public List<Integer> getShowAreaNums() {
        return showAreaNums;
    }

    public void setShowAreaNums(List<Integer> showAreaNums) {
        this.showAreaNums = showAreaNums;
    }
}
