package com.hdpfans.plugin.spider;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.hdpfans.plugin.model.ProxyResultModel;
import com.hdpfans.plugin.model.UrlCachedModel;

import java.util.Map;

/**
 * 默认代理服务器蜘蛛
 */
public class DefaultProxySpider extends SpiderProxyLeader {

    private SharedPreferences preferences;
    private Gson gson;
    private boolean enableCache = true;

    public DefaultProxySpider(Context context) {
        super(context);
        preferences = context.getSharedPreferences("default_proxy", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("p2p://proxy_dli");
    }

    protected void isEnablCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            UrlCachedModel urlCachedInfo;

            if (enableCache) {
                // get from cached
                String cachedJson = preferences.getString(food, null);
                Pair<String, Map<String, String>> cachedUrl = getCachedUrl(cachedJson);
                if (cachedUrl != null) {
                    return cachedUrl;
                }
            }

            Pair<String, String> proxyAttr = getProxyAttr(food);
            if (proxyAttr != null) {
                ProxyResultModel proxyResult = getProxyResult(proxyAttr.first, proxyAttr.second);
                if (!TextUtils.isEmpty(proxyResult.getUrl())) {
                    if (enableCache) {
                        urlCachedInfo = new UrlCachedModel(proxyResult.getUrl());
                        preferences.edit().putString(food, gson.toJson(urlCachedInfo)).apply();
                    }
                    return new Pair<>(proxyResult.getUrl(), getHeaders(food));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, null);
    }
}

