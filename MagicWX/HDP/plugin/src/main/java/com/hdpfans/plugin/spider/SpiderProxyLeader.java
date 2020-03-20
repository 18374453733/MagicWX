package com.hdpfans.plugin.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.hdp.Fuck;
import com.hdpfans.plugin.model.ProxyResultModel;
import com.hdpfans.plugin.model.UrlCachedModel;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.Response;

public abstract class SpiderProxyLeader extends SpiderBoss {
    /**
     * HDP代理服务器地址
     */
    private static final String PROXY_SERVER = "http://proxy901.juyoufan.net/dli%s/%s?v=2&key=%s";

    /**
     * 缓存时间
     */
    private static final long EXPIRATION_TIME = 60 * 1000 * 20;

    public SpiderProxyLeader(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("p2p://");
    }

    protected Pair<String, Map<String, String>> getCachedUrl(String cachedObjStr) {
        UrlCachedModel urlCachedInfo;
        if (!TextUtils.isEmpty(cachedObjStr)) {
            urlCachedInfo = new Gson().fromJson(cachedObjStr, UrlCachedModel.class);
            if (System.currentTimeMillis() - urlCachedInfo.getTime() <= EXPIRATION_TIME) {
                return new Pair<>(urlCachedInfo.getUrl(), null);
            }
        }
        return null;
    }

    protected Pair<String, String> getProxyAttr(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("p2p://proxy_dli")) {
            Matcher matcher = Pattern.compile("p2p://proxy_dli(\\S+)/(\\S+)").matcher(url);
            if (matcher.find()) {
                String type = matcher.group(1);
                String value = matcher.group(2);
                return new Pair<>(type, value);
            }
        }
        return null;
    }

    protected ProxyResultModel getProxyResult(String type, String value) throws Exception {
        return new Gson().fromJson(getProxyResultString(type, value), ProxyResultModel.class);
    }

    protected String getProxyUrl(String type, String value) {
        return String.format(Locale.getDefault(), PROXY_SERVER, type, value, Fuck.g(getContext(), value));
    }

    protected String getProxyResultString(String type, String value) throws Exception {
        String proxyUrl = getProxyUrl(type, value);
        Request request = new Request.Builder().url(proxyUrl).build();
        Response response = getOkHttpClient().newCall(request).execute();
        return response.body().string();
    }
}
