package com.hdpfans.plugin.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http类型代理蜘蛛
 */
public abstract class SpiderHttpLeader extends SpiderBoss {

    private static final String HTTP_URL_REGEX = "http(\\d+)://(.+)";

    public SpiderHttpLeader(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.matches(HTTP_URL_REGEX);
    }

    protected Pair<String, String> getHttpAttr(String url) {
        if (!TextUtils.isEmpty(url) && hint(url)) {
            Matcher matcher = Pattern.compile(HTTP_URL_REGEX).matcher(url);
            if (matcher.find()) {
                String type = matcher.group(1);
                String value = matcher.group(2);
                return new Pair<>(type, value);
            }
        }
        return null;
    }
}
