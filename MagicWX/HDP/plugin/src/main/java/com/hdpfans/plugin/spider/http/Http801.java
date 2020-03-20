package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.util.Pair;

import java.util.Locale;
import java.util.Map;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

public class Http801 extends SpiderHttpLeader {

    private static final String URL = "http://v.ahtv.cn/m2o/m3u8.php?url=http://%s";

    public Http801(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http801://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {

            String url = String.format(Locale.getDefault(), URL, getHttpAttr(food).second);
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0")
                    .header("Referer", "http://v.ahtv.cn/live/")
                    .header("X-Requested-With", "ShockwaveFlash/23.0.0.162")
                    .build();
            food = getOkHttpClient().newCall(request).execute().body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }
}
