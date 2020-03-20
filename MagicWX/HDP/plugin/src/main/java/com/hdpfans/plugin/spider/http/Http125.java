package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.util.Pair;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

public class Http125 extends SpiderHttpLeader {

    private static final String URL = "http://info.zb.qq.com/?cnlid=%s&host=qq.com&cmd=2&qq=0&guid=63929914d4368f22d74e018d9243242d&txvjsv=2.0&stream=2&debug=&ip=&system=2&sdtfrom=313";

    public Http125(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http125://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            String url = String.format(Locale.getDefault(), URL, getHttpAttr(food).second);
            String result = getOkHttpClient().newCall(new Request.Builder().url(url).build()).execute().body().string();
            food = new JSONObject(result).getString("playurl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }
}
