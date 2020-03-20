package com.hdpfans.plugin.compat;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hdpfans.plugin.model.RegionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegionCompat {

    private static final String REGION_URL = "http://ip.taobao.com/service/getIpInfo.php?ip=myip";

    private static final String PREF_KEY_REGION = "pref.region";

    private RegionModel mRegionModel;

    private SharedPreferences sharedPreferences;

    public static RegionCompat sInstance;

    public static RegionCompat getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (RegionCompat.class) {
                if (sInstance == null) {
                    sInstance = new RegionCompat(context);
                }
            }
        }
        return sInstance;
    }


    private RegionCompat(final Context context) {
        sharedPreferences = context.getSharedPreferences("region", Context.MODE_PRIVATE);

        Request request = new Request.Builder()
                .url(REGION_URL)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    RegionModel regionModel = new RegionModel();
                    JSONObject regionJson = new JSONObject(response.body().string()).getJSONObject("data");
                    regionModel.setCountry(regionJson.optString("country"));
                    regionModel.setCity(regionJson.optString("city"));
                    regionModel.setIsp(regionJson.optString("isp"));
                    regionModel.setRegion(regionJson.optString("region"));
                    mRegionModel = regionModel;

                    sharedPreferences.edit().putString(PREF_KEY_REGION, new Gson().toJson(mRegionModel)).apply();

                    // 初始化白名单
                    WhitelistCompat.getInstance().build(context, regionModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public RegionModel getRegionModel() {
        if (mRegionModel == null) {
            mRegionModel = new Gson().fromJson(sharedPreferences.getString(PREF_KEY_REGION, new Gson().toJson(mRegionModel)), RegionModel.class);
        }
        return mRegionModel;
    }
}

