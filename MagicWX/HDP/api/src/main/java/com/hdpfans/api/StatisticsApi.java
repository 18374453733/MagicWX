package com.hdpfans.api;

import com.hdpfans.api.annotation.Plugin;

import org.json.JSONObject;

@Plugin(main = "com.hdpfans.plugin.StatisticsPluginApi", pack = "hdp.jar")
public interface StatisticsApi extends Api {

    void onPlayChannel(JSONObject channel);

    void onStopChannel(JSONObject channel, long time);

    void onExitApp();
}
