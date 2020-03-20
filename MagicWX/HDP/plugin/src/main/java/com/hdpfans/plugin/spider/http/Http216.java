package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.io.IOException;
import java.util.Map;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

@Deprecated
public class Http216 extends SpiderHttpLeader {

    private static final String URL = "http://www.sitv.com.cn/GetPlayPath/GetPlayPath?type=LIVE&code=&se=sitv&ct=2&ip=244.123.6.3";

    public Http216(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http216://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {

        try {
            String result = getOkHttpClient().newCall(new Request.Builder().url(URL).build()).execute().body().string();
            if (!TextUtils.isEmpty(result)) {
                food = result.replaceAll("2300000", "1300000");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(food, getHeaders(food));
    }
}
