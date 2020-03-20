package com.hdpfans.plugin.spider.proxy;

import android.content.Context;

import com.hdpfans.plugin.spider.DefaultProxySpider;

public class AiShangDliSpider extends DefaultProxySpider {

    public AiShangDliSpider(Context context) {
        super(context);
        isEnablCache(false);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("p2p://proxy_dli0120");
    }
}
