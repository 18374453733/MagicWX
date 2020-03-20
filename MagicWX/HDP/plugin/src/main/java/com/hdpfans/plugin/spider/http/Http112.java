package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

import static com.hdpfans.plugin.utils.Utils.md5;

public class Http112 extends SpiderHttpLeader {

    private static final String IQIYI_HTML_URL = "http://www.iqiyi.com/";

    private static final String IQIYI_API_URL = "http://cache.video.ptqy.gitv.tv/liven/%s?lp=&src=04022001010000000000&pf=9&m=531&qyid=tv_c1829f04fa151f08c9198635c11ca13e_%s&vf=%s";

    private static final String REGEX_CHANNEL_ID = "channelId\\s*=\\s*(\\d+)";

    public Http112(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http112://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        if (!food.startsWith(IQIYI_HTML_URL)) {
            food = IQIYI_HTML_URL + getHttpAttr(food).second + ".html";
        }

        try {
            String apiUrl = getApiUrl(food);
            Request request = new Request.Builder()
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .url(apiUrl)
                    .build();
            String result = getOkHttpClient().newCall(request).execute().body().string();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("streams");
            if (jsonArray != null && jsonArray.length() > 0) {
                food = jsonArray.getJSONObject(jsonArray.length() - 1).getString("url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(food, getHeaders(food));
    }

    private String getApiUrl(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36")
                .build();
        String result = getOkHttpClient().newCall(request).execute().body().string();
        Matcher matcher = Pattern.compile(REGEX_CHANNEL_ID).matcher(result);
        String channelId = null;
        if (matcher.find()) {
            channelId = matcher.group(1);
        }
        if (TextUtils.isEmpty(channelId)) {
            throw new Exception();
        }
        long currentTimeMillis = System.currentTimeMillis();
        String token = md5(channelId + "d5fb4bd9d50c4be6948c97edd7254b0e" + currentTimeMillis);
        return String.format(Locale.getDefault(), IQIYI_API_URL, channelId, currentTimeMillis, token);
    }
}
