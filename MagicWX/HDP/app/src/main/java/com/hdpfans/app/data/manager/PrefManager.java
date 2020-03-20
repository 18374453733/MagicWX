package com.hdpfans.app.data.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hdpfans.app.model.annotation.BootChannelMode;
import com.hdpfans.app.model.annotation.DisplayTextSizeMode;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PrefManager {

    private SharedPreferences preferences;

    @Inject
    public PrefManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setCurrentChannelVersion(int currentChannelVersion) {
        preferences.edit().putInt(PrefKeys.PREF_CURRENT_CHANNEL_VERSION, currentChannelVersion).apply();
    }

    public int getCurrentChannelVersion() {
        return preferences.getInt(PrefKeys.PREF_CURRENT_CHANNEL_VERSION, 0);
    }

    public int getRrecentlyPlayedChannelId() {
        return preferences.getInt(PrefKeys.PREF_RECENTLY_PLAYED_CHANNEL_ID, 0);
    }

    public void setRrecentlyPlayedChannelId(int channelId) {
        preferences.edit().putInt(PrefKeys.PREF_RECENTLY_PLAYED_CHANNEL_ID, channelId).apply();
    }

    public boolean getRegionChannelVisibility() {
        return preferences.getBoolean(PrefKeys.PREF_REGION_CHANNEL_VISIBILITY, true);
    }

    public void setRegionChannelVisibility(boolean show) {
        preferences.edit().putBoolean(PrefKeys.PREF_REGION_CHANNEL_VISIBILITY, show).apply();
    }

    public void setBootChannelMode(@BootChannelMode int mode) {
        preferences.edit().putInt(PrefKeys.PREF_BOOT_CHANNEL_MODE, mode).apply();
    }

    @BootChannelMode
    public int getBootChannelMode() {
        return preferences.getInt(PrefKeys.PREF_BOOT_CHANNEL_MODE, 0);
    }

    public void setDisplayTextSizeMode(@DisplayTextSizeMode int textSizeMode) {
        preferences.edit().putInt(PrefKeys.PREF_DISPLAY_TEXT_SIZE_MODE, textSizeMode).apply();
    }

    @DisplayTextSizeMode
    public int getDisplayTextSizeMode() {
        return preferences.getInt(PrefKeys.PREF_DISPLAY_TEXT_SIZE_MODE, DisplayTextSizeMode.MIDDLE);
    }

    public boolean isOpenChannelEpg() {
        return preferences.getBoolean(PrefKeys.PREF_OPEN_CHANNEL_EPG, true);
    }

    public void setOpenChannelEpg(boolean open) {
        preferences.edit().putBoolean(PrefKeys.PREF_OPEN_CHANNEL_EPG, open).apply();
    }

    public boolean isOpenSystemBoot() {
        return preferences.getBoolean(PrefKeys.PREF_OPEN_SYSTEM_BOOT, false);
    }

    public void setOpenSystemBoot(boolean open) {
        preferences.edit().putBoolean(PrefKeys.PREF_OPEN_SYSTEM_BOOT, open).apply();
    }

    public boolean isOpenVoiceSupport() {
        return preferences.getBoolean(PrefKeys.PREF_OPEN_VOICE_SUPPORT, true);
    }

    public void setOpenVoiceSupport(boolean open) {
        preferences.edit().putBoolean(PrefKeys.PREF_OPEN_VOICE_SUPPORT, open).apply();
    }

    public void setSelectedRegion(String region) {
        preferences.edit().putString(PrefKeys.PREF_SELECTED_REGION, region).apply();
    }

    public String getSelectedRegion() {
        return preferences.getString(PrefKeys.PREF_SELECTED_REGION, null);
    }

    public void setLaunchImage(String url) {
        preferences.edit().putString(PrefKeys.PREF_LAUNCH_IMAGE, url).apply();
    }

    public String getLaunchImage() {
        return preferences.getString(PrefKeys.PREF_LAUNCH_IMAGE, null);
    }
}
