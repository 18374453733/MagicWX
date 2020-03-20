package com.hdpfans.plugin.spider;

import android.content.Context;
import android.util.Pair;

import java.util.Map;

/**
 * 未知的代理蜘蛛
 */
public class StraySpider extends SpiderBoss {

    public StraySpider(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return true;
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        return new Pair<>(food, null);
    }
}
