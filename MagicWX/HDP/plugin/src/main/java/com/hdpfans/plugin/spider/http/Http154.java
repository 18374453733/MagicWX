package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.util.Pair;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

import static com.hdpfans.plugin.utils.Utils.md5;

public class Http154 extends SpiderHttpLeader {

    private static final String HLS_LIVE_KEY = "https://hls-api.cutv.com/getCutvHlsLiveKey?t=%s&id=%s&token=%s";

    public Http154(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http154://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {

            String time = String.valueOf(System.currentTimeMillis() / 1000);
            String token = md5(time + getHttpAttr(food).second + "cutvLiveStream|Dream2017");
            String url = String.format(Locale.getDefault(), HLS_LIVE_KEY, time, getHttpAttr(food).second, token);
            Request request = new Request.Builder()
                    .url(url)
                    .header("X-Tingyun-Id", "kIocKK4RzEE;c=2;r=" + m6339H() + ";")
                    .header("X-Tingyun-Lib-Type-N-ST", "2;" + time)
                    .build();
            String result = getOkHttpClient().newCall(request).execute().body().string();
            food = getLiveEncryptUrl(getHttpAttr(food).second, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }

    private String getLiveEncryptUrl(String id, String key) {
        long time = System.currentTimeMillis() / 1000 + 7200;
        String hexTime = Long.toHexString(time);
        String preUrl = "http://sztv-hls.cutv.com/" + id + "/500/" + key + ".m3u8";
        String keyCode = "bf9b2cab35a9c38857b82aabf99874aa96b9ffbb";
        String md5 = md5(keyCode + "/" + id + "/500/" + key + ".m3u8" + hexTime);
        return preUrl + "?sign=" + md5 + "&t=" + hexTime;
    }

    private int m6339H() {
        int nextInt = new Random().nextInt();
        if (nextInt < 0) {
            return -nextInt;
        }
        if (nextInt < 100) {
            return nextInt + 100;
        }
        return nextInt;
    }
}
