package com.hdpfans.plugin.spider.proxy;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import com.hdpfans.plugin.spider.DefaultProxySpider;

public class Dli16Spider extends DefaultProxySpider {

    public Dli16Spider(Context context) {
        super(context);
    }

    @Override
    public Map<String, String> getHeaders(final String food) {
        return new HashMap<String, String>() {{
            put("User-Agent", "AppleCoreMedia/1.0.0.15F79 (iPhone; U; CPU OS 11_4 like Mac OS X; zh_cn)");
            put("Referer", "http://mapi.cbg.cn/live/detail?id=" + getProxyAttr(food).second + "&redirectUrl=http%3A%5C%2F%5C%2Fmapi.cbg.cn%2Flive%2Flist%2Fcqweb");
        }};
    }
}
