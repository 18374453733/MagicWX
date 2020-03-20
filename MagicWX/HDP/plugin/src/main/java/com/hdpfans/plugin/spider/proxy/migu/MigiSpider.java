package com.hdpfans.plugin.spider.proxy.migu;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hdpfans.plugin.spider.SpiderProxyLeader;
import com.hdpfans.plugin.model.UrlCachedModel;
import okhttp3.Request;

public class MigiSpider extends SpiderProxyLeader {

    private static final String MIGI_URL = "http://migu.cmvideo.cn/clt50/publish/clt/resource/miguvideo4/player/playerData.jsp?contentId=%s&nodeId=&objType=videolive&nt=4&netType=WLAN&rate=5&sdkVersion=24.00.01.00&playerType=4&res=HDPI&filterType=3&%s";

    private SharedPreferences preferences;
    private Gson gson;

    public MigiSpider(Context context) {
        super(context);
        preferences = context.getSharedPreferences("Migi", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("p2p://migi");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {

        try {
            UrlCachedModel urlCachedInfo;

            // get from cached
            String cachedJson = preferences.getString(food, null);
            Pair<String, Map<String, String>> cachedUrl = getCachedUrl(cachedJson);
            if (cachedUrl != null) {
                return cachedUrl;
            }

            Pair<String, String> proxyAttr = getProxyAttr(food);
            if (proxyAttr != null) {
                String auth = getProxyResultString(proxyAttr.first, proxyAttr.second);
                String url = getMigiUrl(proxyAttr.second, auth);
                // cache
                urlCachedInfo = new UrlCachedModel(url);
                preferences.edit().putString(food, gson.toJson(urlCachedInfo)).apply();
                return new Pair<>(url, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, null);
    }

    @Override
    protected Pair<String, String> getProxyAttr(String url) {
        Matcher matcher = Pattern.compile("p2p://migi_(\\S+)").matcher(url);
        if (matcher.find()) {
            String type = "0115";
            String value = matcher.group(1);
            return new Pair<>(type, value);
        }
        return null;
    }

    private String getMigiUrl(String channelId, String auth) throws IOException {
        Request request = new Request.Builder()
                .url(String.format(Locale.getDefault(), MIGI_URL, channelId, auth))
                .addHeader("X_UP_CLIENT_ID", "000250")
                .addHeader("X_UP_CLIENT_CHANNEL_ID", "24000105-99000-200300220100002")
                .build();
        String migiResult = getOkHttpClient().newCall(request).execute().body().string();
        // 处理url
        String playUrl = getPlayUrlFromErrorJson(migiResult);
        playUrl = playUrl.replaceFirst("gslbmgsplive.miguvideo.com", "live.hcs.cmvideo.cn:8088");
        playUrl += "&jid=" + md5(System.currentTimeMillis() + channelId);
        return playUrl;
    }

    /**
     * migi的json数据有误，通过正则去匹配获取
     */
    private String getPlayUrlFromErrorJson(String errorJson) {
        Matcher matcher = Pattern.compile("\"playUrl\":\"(\\S+)\"").matcher(errorJson);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
