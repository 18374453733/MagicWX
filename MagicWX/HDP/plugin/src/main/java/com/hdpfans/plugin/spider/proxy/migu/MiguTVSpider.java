package com.hdpfans.plugin.spider.proxy.migu;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.hdpfans.plugin.model.MiguTVRatesModel;
import com.hdpfans.plugin.model.MiguTVResultModel;
import com.hdpfans.plugin.model.TokenCachedInfoModel;
import com.hdpfans.plugin.spider.SpiderProxyLeader;
import com.hdpfans.plugin.model.ProxyResultModel;
import com.hdpfans.plugin.model.UrlCachedModel;
import okhttp3.Request;

public class MiguTVSpider extends SpiderProxyLeader {

    private static final String TOKEN_URL = "https://tv.miguvideo.com/api/clientLogin?cid=%s&time=%s&channelId=";

    private static final String PROXY_URL = "%s&token=%s&time=%s&clientid=%s";

    private static final String TOKEN_KEY = "token";

    /**
     * Token缓存时间
     */
    protected static final long TOKEN_EXPIRATION_TIME = 60 * 1000 * 30;

    private SharedPreferences preferences;
    private Gson gson;

    public MiguTVSpider(Context context) {
        super(context);
        preferences = context.getSharedPreferences("MiguTV", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("p2p://proxy_dli0116");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            UrlCachedModel urlCachedInfo;

            // get url from cached
            String cachedJson = preferences.getString(food, null);
            Pair<String, Map<String, String>> cachedUrl = getCachedUrl(cachedJson);
            if (cachedUrl != null) {
                return cachedUrl;
            }

            // get token from cached
            String cid = "", loginTime = "", token = "";
            String cachedToken = preferences.getString(TOKEN_KEY, null);
            if (!TextUtils.isEmpty(cachedToken)) {
                TokenCachedInfoModel tokenCachedInfo = gson.fromJson(cachedToken, TokenCachedInfoModel.class);
                if (System.currentTimeMillis() - tokenCachedInfo.getTime() <= TOKEN_EXPIRATION_TIME) {
                    cid = tokenCachedInfo.getCid();
                    loginTime = tokenCachedInfo.getLoginTime();
                    token = tokenCachedInfo.getToken();
                }
            }

            if (TextUtils.isEmpty(cid) || TextUtils.isEmpty(loginTime) || TextUtils.isEmpty(token)) {
                cid = "H_" + (new Random().nextInt((999999 - 1000) + 1) + 1000);
                loginTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                token = getToken(cid, loginTime);

                // save token info
                TokenCachedInfoModel tokenCachedInfo = new TokenCachedInfoModel(token, cid, loginTime);
                preferences.edit().putString(TOKEN_KEY, gson.toJson(tokenCachedInfo)).apply();
            }

            Pair<String, String> proxyAttr = getProxyAttr(food);
            if (proxyAttr != null) {
                String miguTVUrl = getProxyResult(getProxyUrl(proxyAttr.first, proxyAttr.second), token, loginTime);
                Request request = new Request.Builder()
                        .url(miguTVUrl)
                        .addHeader("x-migutv-cid", cid)
                        .addHeader("x-migutv-logintime", loginTime)
                        .addHeader("x-migutv-token", token)
                        .addHeader("Referer", "https://tv.miguvideo.com/?")
                        .build();
                MiguTVResultModel miguTVResult = gson.fromJson(getOkHttpClient().newCall(request).execute().body().string(), MiguTVResultModel.class);
                if (miguTVResult.getCode() == 200) {
                    List<MiguTVRatesModel> rates = miguTVResult.getBody().getRates();
                    Collections.sort(rates, new Comparator<MiguTVRatesModel>() {
                        @Override
                        public int compare(MiguTVRatesModel o1, MiguTVRatesModel o2) {
                            return o2.getRateValue() - o1.getRateValue();
                        }
                    });

                    String url = null;
                    for (MiguTVRatesModel rate : rates) {
                        if (!TextUtils.isEmpty(rate.getRateUrl())) {
                            url = rate.getRateUrl();
                            break;
                        }
                    }

                    if (!TextUtils.isEmpty(url)) {
                        // save url
                        urlCachedInfo = new UrlCachedModel(url);
                        preferences.edit().putString(food, gson.toJson(urlCachedInfo)).apply();
                        return new Pair<>(url, null);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, null);
    }

    private String getToken(String cid, String loginTime) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(String.format(Locale.getDefault(), TOKEN_URL, cid, loginTime))
                .build();
        JSONObject resultJson = new JSONObject(getOkHttpClient().newCall(request).execute().body().string());
        return resultJson.getJSONObject("body").getString("token");
    }

    private String getProxyResult(String url, String token, String loginTime) throws IOException {
        Request request = new Request.Builder()
                .url(String.format(Locale.getDefault(), PROXY_URL, url ,token, loginTime, UUID.randomUUID().toString().replace("-", "")))
                .build();
        return gson.fromJson(getOkHttpClient().newCall(request).execute().body().string(), ProxyResultModel.class).getUrl();
    }

}
