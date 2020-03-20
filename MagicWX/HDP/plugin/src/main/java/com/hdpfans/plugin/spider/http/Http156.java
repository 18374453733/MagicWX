package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.FormBody;
import okhttp3.Request;

public class Http156 extends SpiderHttpLeader {

    private static final String GET_HOST_URL = "http://14.204.84.51:88/ynapp/api?reqNo=9906";

    private static final String GET_PATH_URL = "http://14.204.84.51:88/ynapp/api?reqNo=3011";

    private Map<String, String> map = new HashMap<>();

    private String mLiveUrlHost = "http://14.204.84.49:11223";

    public Http156(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http156://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            if (map.isEmpty()) {
                mLiveUrlHost = new JSONObject(request(GET_HOST_URL)).getString("live");
                if (!TextUtils.isEmpty(mLiveUrlHost) && mLiveUrlHost.contains("http")) {
                    String result = "";
                    for (int i = 0; i < 5; i++) {
                        result = request(GET_PATH_URL);
                        if (!TextUtils.isEmpty(result)) break;
                    }

                    JSONArray liveArr = new JSONObject(result).getJSONArray("live");
                    if (liveArr != null) {
                        for (int i = 0; i < liveArr.length(); i++) {
                            JSONObject jsonObject = liveArr.getJSONObject(i);
                            if (jsonObject != null) {
                                String id = jsonObject.optString("id");
                                String url = jsonObject.optString("url");
                                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(url)) {
                                    map.put(id, url);
                                }
                            }
                        }
                    }
                }
            }
            String url = map.get(getHttpAttr(food).second);
            if (url.contains("playlist.m3u8")) {
                url = url.replace("playlist.m3u8", "chunklist.m3u8");
            }
            food = mLiveUrlHost + Uri.parse(url).getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }

    private String request(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "ios11.0.3_v342#3.4.2_w375_h667_m22fbf" + md5(String.valueOf(new Random().nextDouble())) + "756_piPhone7,2")
                .header("Accept-Language", "zh-Hans-CN;q=1, zh-Hant-CN;q=0.9, en-US;q=0.8")
                .post(new FormBody.Builder().build())
                .build();
        return getOkHttpClient().newCall(request).execute().body().string();
    }

    private String md5(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
            StringBuilder stringBuilder = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                if ((b & 255) < 16) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toHexString(b & 255));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
