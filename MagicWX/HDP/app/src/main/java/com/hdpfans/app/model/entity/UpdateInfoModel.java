package com.hdpfans.app.model.entity;

import java.util.List;

public class UpdateInfoModel {

    /**
     * Apk节点
     */
    private ApkInfoModel apkInfo;

    /**
     * 频道节点
     */
    private ChannelInfoModel channelInfo;

    /**
     * 插件节点
     */
    private List<PluginModel> plugins;

    /**
     * 购物频道底图节点
     */
    private List<ShopReproductionModel> shopReproduction;

    /**
     * 频道角标节点
     */
    private List<ChannelMarkModel> channelMark;

    /**
     * 屏蔽推荐信息
     */
    private FlavorRecommend blockRecommends;

    /**
     * 退出推荐信息
     */
    private FlavorRecommend exitRecommends;

    public ApkInfoModel getApkInfo() {
        return apkInfo;
    }

    public void setApkInfo(ApkInfoModel apkInfo) {
        this.apkInfo = apkInfo;
    }

    public ChannelInfoModel getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfoModel channelInfo) {
        this.channelInfo = channelInfo;
    }

    public List<PluginModel> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginModel> plugins) {
        this.plugins = plugins;
    }

    public List<ShopReproductionModel> getShopReproduction() {
        return shopReproduction;
    }

    public void setShopReproduction(List<ShopReproductionModel> shopReproduction) {
        this.shopReproduction = shopReproduction;
    }

    public List<ChannelMarkModel> getChannelMark() {
        return channelMark;
    }

    public void setChannelMark(List<ChannelMarkModel> channelMark) {
        this.channelMark = channelMark;
    }

    public FlavorRecommend getBlockRecommends() {
        return blockRecommends;
    }

    public void setBlockRecommends(FlavorRecommend blockRecommends) {
        this.blockRecommends = blockRecommends;
    }

    public FlavorRecommend getExitRecommends() {
        return exitRecommends;
    }

    public void setExitRecommends(FlavorRecommend exitRecommends) {
        this.exitRecommends = exitRecommends;
    }
}
