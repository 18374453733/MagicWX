package com.hdpfans.plugin.spider.proxy.migu;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.hdpfans.plugin.model.MiguResultModel;
import com.hdpfans.plugin.model.MiguUrlBodyModel;
import com.hdpfans.plugin.model.MiguUrlInfoModel;
import com.hdpfans.plugin.spider.SpiderProxyLeader;
import com.hdpfans.plugin.model.ProxyResultModel;
import com.hdpfans.plugin.model.UrlCachedModel;
import okhttp3.Request;

public class MiguXIIISpider extends SpiderProxyLeader {

    private SharedPreferences preferences;
    private Gson gson;

    public MiguXIIISpider(Context context) {
        super(context);
        preferences = context.getSharedPreferences("MiguXIII", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("p2p://proxy_dli1008613");
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
                ProxyResultModel proxyResult = getProxyResult(proxyAttr.first, proxyAttr.second);
                String url = getUrl(gson.fromJson(getMiguResult(proxyResult.getUrl(), proxyResult.getHeaders()), MiguResultModel.class));

                urlCachedInfo = new UrlCachedModel(url);
                preferences.edit().putString(food, gson.toJson(urlCachedInfo)).apply();
                return new Pair<>(url, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(food, null);
    }

    /**
     * 请求咪咕服务器
     */
    private String getMiguResult(String url, List<String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        for (String header : headers) {
            String[] headerAttr = header.split(":");
            builder.addHeader(headerAttr[0], headerAttr[1]);
        }
        String result = getOkHttpClient().newCall(builder.build()).execute().body().string();
        if (TextUtils.isEmpty(result)) {
            throw new Resources.NotFoundException();
        }
        return result;
    }

    /**
     * 从咪咕的json获取分辨率最高的url
     */
    private String getUrl(MiguResultModel miguResult) throws NetworkErrorException {
        if (miguResult.getCode() != 200) {
            throw new NetworkErrorException();
        }

        MiguUrlBodyModel miguResultBody = miguResult.getBody();
        if (miguResultBody.getUrlInfos() == null || miguResultBody.getUrlInfos().isEmpty()) {
            return miguResultBody.getUrlInfo().getUrl();
        } else {
            Collections.sort(miguResultBody.getUrlInfos(), new Comparator<MiguUrlInfoModel>() {
                @Override
                public int compare(MiguUrlInfoModel o1, MiguUrlInfoModel o2) {
                    return o2.getRateType() - o1.getRateType();
                }
            });
            return miguResultBody.getUrlInfos().get(0).getUrl();
        }
    }
}
