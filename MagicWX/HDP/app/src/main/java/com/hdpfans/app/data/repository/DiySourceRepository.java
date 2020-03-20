package com.hdpfans.app.data.repository;

import com.hdpfans.api.RemoteApi;
import com.hdpfans.app.data.dao.ChannelDao;
import com.hdpfans.app.data.dao.ChannelTypeDao;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;
import com.hdpfans.app.utils.Optional;
import com.hdpfans.app.utils.ReactivexCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Diy频道仓库
 */
@Singleton
public class DiySourceRepository {

    private static final int DEFAULT_REMOTE_PORT = 12321;

    /**
     * DIY源分类ID
     */
    private static final int DIY_SOURCE_TYPE_ID_1 = 2007;
    private static final int DIY_SOURCE_TYPE_ID_2 = 2006;
    private static final int DIY_SOURCE_TYPE_ID_3 = 2005;

    /**
     * DIY源分类下最小ID
     */
    private static final int DIY_SOURCE_MIN_ID_1 = 94600;
    private static final int DIY_SOURCE_MIN_ID_2 = 95600;
    private static final int DIY_SOURCE_MIN_ID_3 = 96600;

    @Inject
    ChannelDao mChannelDao;
    @Inject
    ChannelTypeDao mChannelTypeDao;
    @Inject
    PluginLoader mPluginLoader;

    @Inject
    public DiySourceRepository() {
    }

    /**
     * 自定义源1/2覆盖，自定义源3追加
     */
    public Completable insertDiySource(int diyId, LinkedHashMap<String, String> diySourceMap) {
        return mChannelDao.getChannelsByType(getDiySourceTypeId(diyId))
                .doOnSuccess(channelModels -> {
                    ChannelTypeModel channelTypeModel = new ChannelTypeModel();
                    channelTypeModel.setId(getDiySourceTypeId(diyId));
                    channelTypeModel.setName("自定义" + diyId);
                    mChannelTypeDao.insertOrUpdateChannelType(channelTypeModel);
                })
                .flatMap(channelModels -> {
                    if (channelModels.size() != 0) {
                        switch (diyId) {
                            case 1:
                            case 2:
                                mChannelDao.delete(channelModels.toArray(new ChannelModel[0]));
                        }
                    }
                    switch (diyId) {
                        case 1:
                            return Single.just(sourceMapToList(diySourceMap, DIY_SOURCE_MIN_ID_1, getDiySourceTypeId(diyId)));
                        case 2:
                            return Single.just(sourceMapToList(diySourceMap, DIY_SOURCE_MIN_ID_2, getDiySourceTypeId(diyId)));
                        case 3:
                        default:
                            int minId = DIY_SOURCE_MIN_ID_3;
                            if (!channelModels.isEmpty()) {
                                minId = channelModels.get(channelModels.size() - 1).getId();
                            }
                            return Single.just(sourceMapToList(diySourceMap, minId, getDiySourceTypeId(diyId)));

                    }
                })
                .doOnSuccess(channelModels -> mChannelDao.insertOrUpdateChannel(channelModels.toArray(new ChannelModel[0])))
                .compose(ReactivexCompat.singleThreadSchedule())
                .ignoreElement();
    }

    private List<ChannelModel> sourceMapToList(Map<String, String> diySourceMap, int minId, int typeId) {
        List<ChannelModel> diySourceList = new ArrayList<>();
        if (diySourceMap != null && !diySourceMap.isEmpty()) {
            for (String name : diySourceMap.keySet()) {
                ChannelModel channelModel = new ChannelModel();
                channelModel.setId(minId);
                channelModel.setNum(minId);
                channelModel.setName(name);
                channelModel.setItemId(typeId);
                channelModel.setTmpUrls(diySourceMap.get(name));
                diySourceList.add(channelModel);

                minId++;
            }
        }
        return diySourceList;
    }

    private int getDiySourceTypeId(int diyId) {
        switch (diyId) {
            case 1:
                return DIY_SOURCE_TYPE_ID_1;
            case 2:
                return DIY_SOURCE_TYPE_ID_2;
            case 3:
            default:
                return DIY_SOURCE_TYPE_ID_3;
        }
    }

    /**
     * 是否是DIY分类
     */
    public boolean isDiyType(int typeId) {
        return typeId == DIY_SOURCE_TYPE_ID_1 || typeId == DIY_SOURCE_TYPE_ID_2 || typeId == DIY_SOURCE_TYPE_ID_3;
    }

    /**
     * 删除DIY节目源，当DIY分类下没有节目源时删除分类
     */
    public void delDiyChannel(ChannelModel channelModel) {
        if (!isDiyType(channelModel.getItemId())) {
            return;
        }

        Single.concat(
                Single.create(emitter -> {
                    mChannelDao.delete(channelModel);
                    emitter.onSuccess(Optional.empty());
                }),
                mChannelDao.getChannelsByType(channelModel.getItemId())
                        .map(List::size)
                        .flatMap(size -> {
                            if (size == 0) {
                                return Single.create(emitter -> mChannelTypeDao.deleteById(channelModel.getItemId()));
                            } else {
                                return Single.just(Optional.empty());
                            }
                        }))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public int getRemotePort() {
        RemoteApi remoteApi = mPluginLoader.createApi(RemoteApi.class);
        if (remoteApi != null) {
            return remoteApi.getRemotePort();
        }
        return DEFAULT_REMOTE_PORT;
    }
}
