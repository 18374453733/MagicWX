package com.hdpfans.plugin.spider.proxy;

import android.content.Context;
import android.util.Pair;

import com.hdp.Fuck;
import com.hdpfans.plugin.spider.DefaultProxySpider;

import java.util.Locale;

@Deprecated
public class LiveDliSpider extends DefaultProxySpider {

    private static final String PROXY_SERVER = "http://live.hdpfans.com/dli%s/%s?v=2&key=%s";

    private static final String DLI_URL_PREFIX = "p2p://188live/dli";

    public LiveDliSpider(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith(DLI_URL_PREFIX);
    }

    @Override
    protected Pair<String, String> getProxyAttr(String url) {
        String[] attr = url.replace(DLI_URL_PREFIX, "").split("/");
        return new Pair<>(attr[0], attr[1]);
    }

    @Override
    protected String getProxyUrl(String type, String value) {
        return String.format(Locale.getDefault(), PROXY_SERVER, type, value, Fuck.g(getContext(), value));
    }
}
