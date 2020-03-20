package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

public class Http167 extends SpiderHttpLeader {

    private static final String IP_SOURCE_URL = "http://play.api.pptv.com/boxplay.api?id=300170&platform=android3";

    private static final String OUTPUT_URL = "http://%s/live/5/30/%s.m3u8?type=m3u8.web.cloudplay";

    private static final String IP_FIND_REGEX = "<sh.*?>(.*?)</sh>";

    private List<String> ipSourceList = new ArrayList<>();

    public Http167(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http167://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        getRequestIP();

        if (!ipSourceList.isEmpty()) {
            int randomIndex = (int) (Math.random() * (ipSourceList.size()));
            food = String.format(Locale.getDefault(), OUTPUT_URL, ipSourceList.get(randomIndex), getHttpAttr(food).second);
        }

        return new Pair<>(food, getHeaders(food));
    }

    private void getRequestIP() {
        try {
            String ipResult = getOkHttpClient().newCall(new Request.Builder().url(IP_SOURCE_URL).build()).execute().body().string();
            if (!TextUtils.isEmpty(ipResult)) {
                Matcher matcher = Pattern.compile(IP_FIND_REGEX).matcher(ipResult);
                if (matcher.find()) {
                    ipSourceList.add(matcher.group(1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
