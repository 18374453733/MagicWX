package com.hdpfans.plugin.spider.shop;

import android.content.Context;
import android.util.Pair;

import java.util.Map;

import com.hdpfans.plugin.spider.SpiderBoss;

public class ShopSpider extends SpiderBoss {

    public ShopSpider(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("shop://")
                || (food.endsWith("sh") && food.length() > 7 && food.substring(2, 7).equals("op://"));
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        if (food.endsWith("sh")) {
            String prefix = food.substring(0, 2);
            String suffix = food.substring(food.length() - 2);
            food = suffix + food.substring(2, food.length() - 2) +prefix;
        }

        if (food.startsWith("shop://diy")) {
            food = food.replaceFirst("shop://diy", "http://");
        } else if (food.startsWith("shop://")) {
            food = food.replaceFirst("shop://", "http://");
        }
        return new Pair<>(food, null);
    }
}
