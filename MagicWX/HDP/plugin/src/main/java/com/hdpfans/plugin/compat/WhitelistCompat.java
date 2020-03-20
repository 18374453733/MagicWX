package com.hdpfans.plugin.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hdpfans.plugin.model.RegionModel;
import com.hdpfans.plugin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 白名单
 */
public class WhitelistCompat {

    private static final String WHITE_LIST_URL = "http://proxy901.juyoufan.net/dli0002/?oskey=%s";

    private static final String KEYS_USER_STATUS = "keys_user_status";

    private static final String KEYS_USER_REGION = "keys_user_region";

    /**
     * 开放城市
     */
    private static final List<String> OPEN_REGIONS = Arrays.asList("河南", "山东", "福建", "广西", "内蒙古", "云南", "贵州", "新疆", "青海", "宁夏", "西藏");

    private static volatile WhitelistCompat sInstance;

    private WhitelistCompat() {
    }

    public static WhitelistCompat getInstance() {
        if (sInstance == null) {
            synchronized (WhitelistCompat.class) {
                if (sInstance == null) {
                    sInstance = new WhitelistCompat();
                }
            }
        }
        return sInstance;
    }

    public void build(final Context context, final RegionModel regionModel) {
        boolean isChangedRegion = false;
        if (getUserStatus(context) == 0) {
            if (!TextUtils.isEmpty(regionModel.getRegion())) {
                String lastUserRegion = getUserRegion(context);

                if (!TextUtils.isEmpty(lastUserRegion)) {
                    // 判断地域是否改变（如有改变直接拉黑）
                    if (!lastUserRegion.equals(regionModel.getRegion())) {
                        setUserStatus(context, -1);
                        isChangedRegion = true;
                    } else {
                        // 地域开放
                        for (String openRegion : OPEN_REGIONS) {
                            if (regionModel.getRegion().contains(openRegion)) {
                                setUserStatus(context, 1);
                                break;
                            }
                        }
                    }
                }

            }
            setUserRegion(context, regionModel.getRegion());
        }

        // 判断国家是否中国（不是中国允许使用）
        if (getUserStatus(context) == 0) {
            if (!TextUtils.isEmpty(regionModel.getCountry()) && !regionModel.getCountry().equals("中国")) {
                setUserStatus(context, 1);
            }
        }

        // 直接屏蔽手机用户
        if (getUserStatus(context) == 0) {
            if (BoxCompat.isPhoneRunning(context)) {
                setUserStatus(context, -1);
            }
        }

        // 不确定用户向服务器验证
        if (getUserStatus(context) == 0 || isChangedRegion) {
            Request request = new Request.Builder()
                    .url(String.format(Locale.getDefault(), WHITE_LIST_URL, Utils.getOsKey(context)))
                    .build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) {
                    JSONObject resultJson;
                    try {
                        resultJson = new JSONObject(response.body().string());
                        int status = resultJson.optInt("status");
                        setUserStatus(context, status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public boolean isInWhitelist(Context context) {
        return getUserStatus(context) == 1;
    }

    /**
     * 获取当前用户状态
     *
     * @return 0:待定 1: 白名单 -1: 黑名单
     */
    private int getUserStatus(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("wt", Context.MODE_PRIVATE);
        return preferences.getInt(KEYS_USER_STATUS, 0);
    }

    private void setUserStatus(Context context, int userStatus) {
        SharedPreferences preferences = context.getSharedPreferences("wt", Context.MODE_PRIVATE);
        preferences.edit().putInt(KEYS_USER_STATUS, userStatus).apply();
    }

    private String getUserRegion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("wt", Context.MODE_PRIVATE);
        return preferences.getString(KEYS_USER_REGION, null);
    }

    private void setUserRegion(Context context, String region) {
        SharedPreferences preferences = context.getSharedPreferences("wt", Context.MODE_PRIVATE);
        preferences.edit().putString(KEYS_USER_REGION, region).apply();
    }

}
