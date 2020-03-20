package com.hdpfans.plugin.spider.proxy.migu;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.hdpfans.plugin.model.UrlCachedModel;
import com.hdpfans.plugin.spider.SpiderProxyLeader;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import okhttp3.Request;

@Deprecated
public class Migu2Spider extends SpiderProxyLeader {

    private static final String MIGU2_PREFIX = "p2p://migu2_";

    private static final String URL = "https://m.miguvideo.com/playurl/v1/play/playurlh5?contId=%s&rateType=3";

    private SharedPreferences preferences;
    private Gson gson;

    public Migu2Spider(Context context) {
        super(context);
        preferences = context.getSharedPreferences("Migu2", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith(MIGU2_PREFIX);
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            // get url from cached
            String cachedJson = preferences.getString(food, null);
            Pair<String, Map<String, String>> cachedUrl = getCachedUrl(cachedJson);
            if (cachedUrl != null) {
                return cachedUrl;
            }

            Request request = new Request.Builder()
                    .url(String.format(Locale.getDefault(), URL, food.replace(MIGU2_PREFIX, "")))
                    .build();
            String result = getOkHttpClient().newCall(request).execute().body().string();
            if (!TextUtils.isEmpty(result)) {
                JSONObject jsonObject = new JSONObject(result);
                if ("200".equals(jsonObject.getString("code"))) {
                    String liveUrl = jsonObject.getJSONObject("body").getJSONObject("urlInfo").getString("url");
                    if (!TextUtils.isEmpty(liveUrl) && liveUrl.startsWith("http:")) {
                        UrlCachedModel urlCachedInfo = new UrlCachedModel(liveUrl);
                        preferences.edit().putString(food, gson.toJson(urlCachedInfo)).apply();
                        return new Pair<>(liveUrl, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(food, getHeaders(null));
    }
}
