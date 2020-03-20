package com.hdpfans.plugin;

import android.content.Context;
import android.util.Log;

import com.hdpfans.api.StatisticsApi;
import com.hdpfans.plugin.epg.EpgFactory;
import com.hdpfans.plugin.report.DailyReporter;

import org.json.JSONObject;

public class StatisticsPluginApi implements StatisticsApi {

    private Context context;

    private JSONObject currentPlayChannel;
    private JSONObject lastPlayChannel;

    private DailyReporter dailyReporter;

    private long playTime;

    @Override
    public void onCreate(Context context) {
        this.context = context;
        this.dailyReporter = new DailyReporter(context);

        dailyReporter.report();
    }

    @Override
    public void onPlayChannel(JSONObject channel) {
        this.currentPlayChannel = channel;
        int channelNum = currentPlayChannel.optInt("num");

        if (lastPlayChannel == null || (channelNum != 0 && lastPlayChannel.optInt("num") != channelNum)) {
            this.playTime = 0;
            dailyReporter.onEvent(
                    DailyReporter.OperateType.CHANGE,
                    String.valueOf(channelNum),
                    channel.optString("name"),
                    String.valueOf(channel.optInt("itemid")),
                    EpgFactory.get().create(context, channel.optString("epgid")).getCurrentEpg()
            );
            Log.d("StatisticsPluginApi", "onPlayChannel : " + channelNum + "-" + channel.optString("name"));
        }
    }

    @Override
    public void onStopChannel(JSONObject channel, long time) {
        this.lastPlayChannel = currentPlayChannel;

        int channelNum = lastPlayChannel.optInt("num");
        playTime += time;
        if (channel.optInt("num") != channelNum) {
            if (playTime > 60) {
                dailyReporter.onEvent(
                        DailyReporter.OperateType.PLAY,
                        String.valueOf(channelNum),
                        currentPlayChannel.optString("name"),
                        String.valueOf(channel.optInt("itemid")),
                        EpgFactory.get().create(context, currentPlayChannel.optString("epgid")).getCurrentEpg(),
                        playTime
                );
                Log.d("StatisticsPluginApi", "onStopChannel : " + channelNum + "-" + currentPlayChannel.optString("name") + "#" + playTime);
            }
        }
    }

    @Override
    public void onExitApp() {
        dailyReporter.onEvent(DailyReporter.OperateType.EXIT);
    }
}
