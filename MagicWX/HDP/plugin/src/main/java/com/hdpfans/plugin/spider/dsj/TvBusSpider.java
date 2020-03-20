package com.hdpfans.plugin.spider.dsj;

import android.content.Context;
import android.util.Pair;

import com.hdpfans.plugin.spider.SpiderBoss;
import com.tvbus.engine.TVCore;

import java.util.Map;

public class TvBusSpider extends SpiderBoss {

    public TvBusSpider(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("tvbus://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        return new Pair<>(TVCore.get(getContext()).playUrl(food), getHeaders(food));
    }

    @Override
    public void excretion() {
        TVCore.stopPlay();
    }
}
