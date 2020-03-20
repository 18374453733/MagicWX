package com.hdpfans.plugin.spider.cntv;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hdpfans.plugin.spider.SpiderProxyLeader;
import okhttp3.Request;

public class CntvLocalSpider extends SpiderProxyLeader {

    private static final String CNTV_LOCAL_PREFIX = "p2p://(.+)_pa(.+)";

    private static final String API_ADDR = "http://vdn.live.cntv.cn/api2/live.do?client=iosapp&channel=%s";

    private static final String ANDROID_API_ADDR = "http://vdn.live.cntv.cn/api2/live.do?client=android&channel=%s";

    public CntvLocalSpider(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.matches(CNTV_LOCAL_PREFIX);
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            Pair<String, String> attr = getAttr(food);
            String name = attr.second;

            if (!name.contains("pa://")) {
                name = "pa://" + name;
            }
            String channelId;
            if (name.contains("_hd")) {
                channelId = name.split("_hd")[1];
            } else {
                channelId = name.split("_p2p_")[1];
            }

            Request request = new Request.Builder()
                    .url(String.format(Locale.getDefault(), API_ADDR, name))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_5; de-de) AppleWebKit/534.15+ (KHTML, like Gecko) Version/5.0.3 Safari/533.19.4")
                    .build();
            String result = getOkHttpClient().newCall(request).execute().body().string();
            String cntvAuth = getCntvAuth(result);
            JSONObject jSONObject = new JSONObject(result);
            // 方案1
            if (jSONObject.has("hls_url")) {
                JSONObject optJSONObject = jSONObject.optJSONObject("hls_url");
                food = optJSONObject.getString("hls1");
                if (food.contains("amode=1") && result.contains("AUTH=")) {
                    food = food.split("AUTH=")[0] + cntvAuth;
                }

                Matcher matcher = Pattern.compile("http://(.*?).m3u8").matcher(food);
                if (matcher.find()) {
                    food = food.replace(matcher.group(1), "hls.cntv.myqcloud.com/live/5213_" + channelId + "/index");
                }
            } else {
                // 方案2
                request = new Request.Builder().url(String.format(Locale.getDefault(), ANDROID_API_ADDR, name)).build();
                result = getOkHttpClient().newCall(request).execute().body().string();
                cntvAuth = getCntvAuth(result);
                food = String.format(Locale.getDefault(), "http://hls.cntv.myqcloud.com/live/5213_%s/index.m3u8?ptype=1&amode=1&%s", channelId, cntvAuth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(food, getHeaders(food));
    }

    private String getCntvAuth(String result) throws Exception {
        if (TextUtils.isEmpty(result)) return null;

        String auth = null;
        JSONObject jSONObject = new JSONObject(result);
        if (jSONObject.has("hls_url")) {
            JSONObject optJSONObject = jSONObject.optJSONObject("hls_url");
            Iterator keys = optJSONObject.keys();
            while (keys.hasNext()) {
                result = optJSONObject.optString((String) keys.next());
                if (!TextUtils.isEmpty(result)) {
                    if (!result.contains("audio") && result.startsWith("http://") && result.contains(".m3u8")) {
                        if (result.contains("AUTH=")) {
                            auth = result.split("AUTH=")[1];
                            if (!TextUtils.isEmpty(auth))
                                break;
                        }
                    }

                }
            }
        }

        if (TextUtils.isEmpty(auth) && jSONObject.has("flv_url")) {
            JSONObject optJSONObject2 = jSONObject.optJSONObject("flv_url");
            Iterator keys = optJSONObject2.keys();
            while (keys.hasNext()) {
                result = optJSONObject2.optString((String) keys.next());
                if (!(TextUtils.isEmpty(result) || !result.startsWith("http://") || result.contains(".pub"))) {
                    if (result.contains("AUTH=")) {
                        auth = result.split("AUTH=")[1];
                        if (!TextUtils.isEmpty(auth))
                            break;
                    }

                }
            }
        }

        if (!TextUtils.isEmpty(auth)) {
            auth = URLEncoder.encode(auth);

            String authUrl = getProxyUrl("0114", "cntvauth") + "&type=0&baseauth=" + auth;
            String authResult = getOkHttpClient().newCall(new Request.Builder().url(authUrl).build()).execute().body().string();

            if (!TextUtils.isEmpty(authResult)) {
                return "AUTH=" + authResult;
            }
        }
        throw new Exception();
    }

    private Pair<String, String> getAttr(String url) {
        Matcher matcher = Pattern.compile(CNTV_LOCAL_PREFIX).matcher(url);
        if (matcher.matches()) {
            return new Pair<>(matcher.group(1), matcher.group(2));
        }
        return new Pair<>(url, null);
    }

}
