package com.hdpfans.plugin.spider.dsj;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

import com.alibaba.wireless.security.jaq.SecuritySign;

import java.util.Locale;
import java.util.Map;

import com.hdpfans.plugin.spider.SpiderBoss;

/**
 * 轮播
 */
public class DsjSpider extends SpiderBoss {

    private static final String DSJ_PREFIX = "p2p://lb_";

    private static final String ORIGIN_URL = "http://api.idianshijia.com/gslb/live?stream_id=%s&ostype=android&hwtype=%s&tm=%d&isp=%s&region=%s&uuid=%s&sign=%s";

    public DsjSpider(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith(DSJ_PREFIX);
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {

            String F = "telecom";
            String G = "510000";
            String H = "63e0ae01-df8f-3856-8542-a62b72abfb52";
            long time = System.currentTimeMillis() / 1000;
            String sign = SecuritySign.g(getId(food), F, G, H, time);

            String model = Build.BRAND;
            if (TextUtils.isEmpty(model)) {
                model = Build.MODEL;
            }
            if (TextUtils.isEmpty(model)) {
                model = "MI_" + Build.MODEL;
            }
            food = String.format(Locale.getDefault(), ORIGIN_URL, getId(food), model, time, F, G, H, sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }

    private String getId(String url) {
        return url.replace(DSJ_PREFIX, "");
    }
}
