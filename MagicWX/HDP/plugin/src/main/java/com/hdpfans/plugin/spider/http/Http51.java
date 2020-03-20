package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.util.Pair;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

public class Http51 extends SpiderHttpLeader {

    private static final String REQUEST_URL = "http://mpp.liveapi.mgtv.com/v1/epg/turnplay/getLivePlayUrlMPP?buss_id=2000001&channel_id=%s&definition=std&ticket=&version=PCclient_5.0&platform=1";


    public Http51(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http51://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        Pair<String, String> httpAttr = getHttpAttr(food);

        if (httpAttr != null) {
            try {
                Request request = new Request.Builder()
                        .url(String.format(Locale.getDefault(), REQUEST_URL, httpAttr.second))
                        .build();
                String result = getOkHttpClient().newCall(request).execute().body().string();
                return new Pair<>(new JSONObject(result).getJSONObject("data").getString("url"), getHeaders(food));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Pair<>(food, null);
    }
}
