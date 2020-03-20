package com.hdpfans.app.model.entity;

/**
 * 配置文件购物频道底图信息
 */
public class ShopReproductionModel {

    /**
     * 购物频道编号
     */
    private int shopNum;

    /**
     * 底图地址
     */
    private String url;

    public int getShopNum() {
        return shopNum;
    }

    public void setShopNum(int shopNum) {
        this.shopNum = shopNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
