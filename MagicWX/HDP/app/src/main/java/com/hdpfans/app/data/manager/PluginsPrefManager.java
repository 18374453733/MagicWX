package com.hdpfans.app.data.manager;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PluginsPrefManager {

    private SharedPreferences preferences;

    @Inject
    public PluginsPrefManager(Context context) {
        preferences = context.getSharedPreferences("plugins", Context.MODE_PRIVATE);
    }

    public void savePluginUpdateTime(String name, String updateTime) {
        preferences.edit().putString(name, updateTime).apply();
    }

    public String getPluginUpdateTime(String name) {
        return preferences.getString(name, "0");
    }
}
