package com.hdpfans.app.data.repository;

import android.content.Context;

import com.hdpfans.app.data.dao.ChannelDao;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.utils.Logger;
import com.iflytek.xiri.AppService;
import com.iflytek.xiri.video.channel.ChannelItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class ApiRepository {

    @Inject
    ChannelDao mChannelDao;
    @Inject
    Context appContext;

    @Inject
    public ApiRepository() {

    }

    /**
     * 获取所有频道json，只包含频道号和频道名称
     */
    public String getAllChannelJson() {
        List<ChannelModel> channelModels = mChannelDao.queryAll().blockingGet();
        JSONArray channelJsonArr = new JSONArray();
        try {
            for (ChannelModel channelModel : channelModels) {
                JSONObject channelJson = new JSONObject();
                channelJson.put("channelNum", channelModel.getNum());
                channelJson.put("channelName", channelModel.getName());

                channelJsonArr.put(channelJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelJsonArr.toString();
    }

    public String getChannelNameByNum(int num) {
        ChannelModel channelModel = mChannelDao.queryChannelByNum(num).blockingGet();
        if (channelModel != null) {
            return channelModel.getName();
        }
        return null;
    }

    public void updateVoiceChannel() {
        mChannelDao.queryAll()
                .toObservable()
                .flatMap(Observable::fromIterable)
                .distinct(ChannelModel::getNum)
                .map(channelModel -> {
                    ChannelItem channelItem = new ChannelItem();
                    channelItem.name = channelModel.getName();
                    channelItem.number = String.valueOf(channelModel.getNum());
                    channelItem.cachehours = 0;
                    return channelItem;
                })
                .toList()
                .subscribe(channelItems -> {
                    AppService.updateTVChannel(appContext, new ArrayList<>(channelItems));
                    Logger.LOGI("ApiRepository", "update channel list: " + channelItems.size());
                });
    }
}
