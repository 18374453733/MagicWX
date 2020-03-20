package com.hdpfans.app.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;
import com.hdpfans.api.HdpApi;
import com.hdpfans.app.data.dao.ChannelDao;
import com.hdpfans.app.data.dao.ChannelTypeDao;
import com.hdpfans.app.data.manager.FileManager;
import com.hdpfans.app.data.manager.PluginsPrefManager;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.model.annotation.BootChannelMode;
import com.hdpfans.app.model.annotation.ChannelUpdateType;
import com.hdpfans.app.model.annotation.DiskCacheName;
import com.hdpfans.app.model.annotation.RecommendType;
import com.hdpfans.app.model.entity.BlockInfoModel;
import com.hdpfans.app.model.entity.BlockVersionChannel;
import com.hdpfans.app.model.entity.ChannelInfoModel;
import com.hdpfans.app.model.entity.ChannelMarkModel;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelSetModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;
import com.hdpfans.app.model.entity.DefaultFlavorRecommend;
import com.hdpfans.app.model.entity.FlavorRecommend;
import com.hdpfans.app.model.entity.PluginModel;
import com.hdpfans.app.model.entity.Recommend;
import com.hdpfans.app.model.entity.ShopReproductionModel;
import com.hdpfans.app.model.entity.SpecialFlavorRecommend;
import com.hdpfans.app.model.entity.UpdateInfoModel;
import com.hdpfans.app.utils.Logger;
import com.hdpfans.app.utils.Optional;
import com.hdpfans.app.utils.PhoneCompat;
import com.hdpfans.app.utils.ReactivexCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import hdpfans.com.BuildConfig;
import hdpfans.com.R;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Okio;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Succeed;

/**
 * HDP数据仓库
 * 1. 获取配置文件，并下载更新插件和频道信息
 * 2. 节目源频道和分类的数据管理
 */
@Singleton
public class HdpRepository {

    private static final String TAG = HdpRepository.class.getSimpleName();

    /**
     * 即时频道ID
     */
    private static final int IMMEDIATE_CHANNEL_ID = 99999;

    /**
     * 即时频道分类
     */
    private final ChannelTypeModel mImmediateChannelType = new ChannelTypeModel(Integer.MAX_VALUE, "即时频道", Integer.MAX_VALUE);

    /**
     * 即时频道信息
     */
    private ChannelModel mImmediateChannel;

    /**
     * 收藏分类
     */
    private final ChannelTypeModel mCollectChannelType = new ChannelTypeModel(Integer.MAX_VALUE - 1, "我的收藏", Integer.MAX_VALUE - 1);

    @Inject
    OssRepository mOssRepository;
    @Inject
    ApiRepository mApiRepository;

    @Inject
    ChannelTypeDao mChannelTypeDao;
    @Inject
    ChannelDao mChannelDao;

    @Inject
    FileManager mFileManager;
    @Inject
    PrefManager mPrefManager;
    @Inject
    PluginsPrefManager mPluginsPrefManager;
    @Inject
    PluginLoader mPluginLoader;

    @Inject
    Gson mGson;
    @Inject
    OkHttpClient mOkHttpClient;

    @Inject
    Context appContext;

    //region Memory Cache
    private Optional<UpdateInfoModel> mCachedUpdateInfo = Optional.empty();

    private Optional<Map<Integer, ShopReproductionModel>> mCachedShopReproduction = Optional.empty();

    private Optional<Map<Integer, BlockInfoModel>> mCachedBlocks = Optional.empty();

    private Optional<Map<Integer, ChannelMarkModel>> mCachedChannelMark = Optional.empty();

    private Optional<List<ChannelTypeModel>> mCachedChannelTypes = Optional.empty();
    //endregion

    @Inject
    public HdpRepository() {
    }

    /**
     * 获取配置文件并缓存相应数据
     */
    public Single<UpdateInfoModel> getUpdateInfo() {
        return Single.create((SingleOnSubscribe<byte[]>) e -> e.onSuccess(mOssRepository.getObjectByUrl("http://update.juyoufan.net/hdp_update.json")))
                .compose(ReactivexCompat.singleThreadSchedule())
                .map(String::new)
                .map(s -> mGson.fromJson(s, UpdateInfoModel.class))
                .doOnSuccess(updateInfoModel -> mCachedUpdateInfo = Optional.of(updateInfoModel))
                // 缓存购物底图信息
                .doOnSuccess(updateInfoModel -> {
                    List<ShopReproductionModel> shopReproduction = updateInfoModel.getShopReproduction();
                    if (shopReproduction != null && !shopReproduction.isEmpty()) {
                        mCachedShopReproduction = Optional.of(Observable.fromIterable(shopReproduction).toMap(ShopReproductionModel::getShopNum).blockingGet());
                    }
                })
                // 缓存频道标签信息
                .doOnSuccess(updateInfoModel -> {
                    List<ChannelMarkModel> channelMark = updateInfoModel.getChannelMark();
                    if (channelMark != null && !channelMark.isEmpty()) {
                        mCachedChannelMark = Optional.of(Observable.fromIterable(channelMark).toMap(ChannelMarkModel::getChannelNum).blockingGet());
                    }
                })
                // 缓存屏蔽信息
                .doOnSuccess(updateInfoModel -> {
                    List<BlockInfoModel> blocks = updateInfoModel.getChannelInfo().getBlock();
                    if (blocks != null && !blocks.isEmpty()) {
                        mCachedBlocks = Optional.of(Observable.fromIterable(blocks).toMap(BlockInfoModel::getChannelNum).blockingGet());
                    }
                })
                // 保存启动图片
                .doOnSuccess(updateInfoModel -> mPrefManager.setLaunchImage(updateInfoModel.getApkInfo().getLaunchImage()));
    }

    /**
     * 获取开机启动频道，支持的启动模式{@link BootChannelMode}
     * 1. 默认：按照配置文件启动
     * 2. 上次观看：上次退出App前最后观看的一个频道
     * 3. 收藏：收藏列表中第一个频道
     */
    public Single<Integer> getBootChannel() {
        return Single.just(mPrefManager.getBootChannelMode())
                .flatMap(mode -> {
                    if (mode == BootChannelMode.DEFAULT && mCachedUpdateInfo.isPresent()) {
                        return Single.just(mCachedUpdateInfo.get().getChannelInfo().getBootChannel());
                    } else if (mode == BootChannelMode.LAST_WATCH) {
                        return Single.just(mPrefManager.getRrecentlyPlayedChannelId());
                    } else {
                        return mChannelDao.getCollectedChannel()
                                .map(channelModels -> {
                                    if (channelModels.size() == 0) {
                                        return 0;
                                    } else {
                                        return channelModels.get(0).getId();
                                    }
                                });
                    }
                })
                .compose(ReactivexCompat.singleThreadSchedule());
    }

    /**
     * 获取节目源频道信息
     */
    public Map<Integer, BlockInfoModel> getBlocksInfo() {
        if (mCachedBlocks.isPresent()) {
            return mCachedBlocks.get();
        }
        return null;
    }

    /**
     * 获取显示区域的频道号
     */
    public List<Integer> getBlockShowAreaChannelNums() {
        if (mCachedUpdateInfo.isPresent()) {
            return mCachedUpdateInfo.get().getChannelInfo().getShowAreaNums();
        }
        return null;
    }

    /**
     * 获取过滤版权节目源信息
     */
    public BlockVersionChannel getBlockVersionChannel() {
        if (mCachedUpdateInfo.isPresent()) {
            return mCachedUpdateInfo.get().getChannelInfo().getBlockVersionChannel();
        }
        return null;
    }

    /**
     * 下载动态插件，其中包含dx文件和so文件
     *
     * @param plugins 插件列表
     */
    public Completable downloadPlugin(List<PluginModel> plugins) {
        return Flowable.fromIterable(plugins)
                // 判断插件是否支持当前版本
                .filter(pluginModel -> {
                    if (TextUtils.isEmpty(pluginModel.getSupportVersion())) {
                        return true;
                    } else {
                        return Version.forIntegers(BuildConfig.VERSION_CODE).satisfies(pluginModel.getSupportVersion());
                    }
                })
                // 判断插件是否支持当前渠道
                .filter(pluginModel -> {
                    if (TextUtils.isEmpty(pluginModel.getSupportFlavor())) {
                        return true;
                    } else {
                        return BuildConfig.FLAVOR.equals(pluginModel.getSupportFlavor());
                    }
                })
                .filter(pluginModel -> !pluginModel.getUpdateTime().equals(mPluginsPrefManager.getPluginUpdateTime(pluginModel.getName())))
                // 判断是否需要更新插件
                .doOnNext(pluginModel -> {
                    File pluginFile;
                    if (pluginModel.getUrl().endsWith("so")) {
                        pluginFile = new File(mFileManager.getSystemLibsDir().getAbsolutePath(), pluginModel.getName());
                    } else {
                        pluginFile = new File(mFileManager.getPluginsDir().getAbsolutePath(), pluginModel.getName());
                    }
                    if (pluginFile.exists()) {
                        pluginFile.delete();
                    }
                })
                .map(pluginModel -> {
                    Response response = mOkHttpClient.newCall(new Request.Builder().url(mOssRepository.getSignUrl(pluginModel.getUrl())).build()).execute();
                    if (response.isSuccessful()) {
                        File saveFile;
                        if (pluginModel.getUrl().endsWith(".so")) {
                            saveFile = new File(mFileManager.getSystemLibsDir().getAbsolutePath(), pluginModel.getName());
                        } else {
                            saveFile = (new File(mFileManager.getPluginsDir().getAbsolutePath(), pluginModel.getName()));
                        }
                        FileOutputStream fos = new FileOutputStream(saveFile);
                        fos.write(response.body().bytes());
                        fos.close();
                    }
                    return pluginModel;
                })
                .toList()
                .compose(ReactivexCompat.singleThreadSchedule())
                .ignoreElement()
                // 存储插件信息
                .doOnComplete(() -> {
                    for (PluginModel plugin : plugins) {
                        mPluginsPrefManager.savePluginUpdateTime(plugin.getName(), plugin.getUpdateTime());
                    }
                });
    }

    /**
     * 更新本地节目源
     * 1. 判断线上与本地节目源版本
     * 2. 下载节目源信息
     * 3. 更新节目源，如果版本差距在10以内采用增量更新，否则采用全量更新
     *
     * @param channelInfo 线上节目源信息
     */
    public Completable updateChannel(ChannelInfoModel channelInfo) {
        if (channelInfo == null) return Completable.error(new NullPointerException());

        int codeDiff = channelInfo.getMaxVersion() - mPrefManager.getCurrentChannelVersion();
        if (codeDiff <= 0) {
            Logger.LOGI(TAG, "Don't need download channel");
            return Completable.complete();
        } else if (codeDiff > 10 || mPrefManager.getCurrentChannelVersion() == 0) {
            Logger.LOGI(TAG, "Download channel version " + channelInfo.getMaxVersion());
            return downloadChannelZip(mOssRepository.getSignUrl(channelInfo.getUrl()), channelInfo.getMaxVersion(), true);
        } else {
            return Single.just(String.format(Locale.getDefault(), "%sV%d-V%d.zip", channelInfo.getIncrementBaseUrl(), mPrefManager.getCurrentChannelVersion(), channelInfo.getMaxVersion()))
                    .compose(ReactivexCompat.singleThreadSchedule())
                    .map(url -> mOssRepository.getSignUrl(url))
                    .flatMap(url -> Single.just(url)
                            .map(u -> mOkHttpClient.newCall(new Request.Builder().url(url).head().build()).execute())
                            .compose(ReactivexCompat.singleThreadSchedule())
                            .flatMap(response -> {
                                // 如果增量不存在而执行全量更新
                                if (response.isSuccessful()) {
                                    return downloadChannelZip(url, channelInfo.getMaxVersion(), false).toSingleDefault(Optional.empty());
                                } else {
                                    return downloadChannelZip(mOssRepository.getSignUrl(channelInfo.getUrl()), channelInfo.getMaxVersion(), true).toSingleDefault(Optional.empty());
                                }
                            }))
                    .ignoreElement();
        }
    }

    /**
     * 下载节目源zip包
     */
    private Completable downloadChannelZip(String url, int maxVersion, boolean isFullAmount) {
        Logger.LOGI(TAG, url);
        Mission mission = new Mission(url, "channel.zip", mFileManager.getDiskCacheDir(DiskCacheName.TMP).getAbsolutePath());
        RxDownload.INSTANCE.create(mission, true);
        return RxDownload.INSTANCE.create(mission, true)
                // 判断下载状态
                .filter(status -> {
                    if (status instanceof Failed) {
                        ((Failed) status).getThrowable().printStackTrace();
                    }
                    return status instanceof Succeed;
                })
                .firstOrError()
                // 解压节目源压缩包
                .flatMap(o -> {
                    File channelFileDir = new File(mFileManager.getDiskCacheDir(DiskCacheName.TMP), "channel");
                    mFileManager.unzipFile(new File(mFileManager.getDiskCacheDir(DiskCacheName.TMP), "channel.zip"), new File(mFileManager.getDiskCacheDir(DiskCacheName.TMP), "channel").getAbsolutePath());
                    return Single.just(channelFileDir);
                })
                // 从文件读取源信息
                .flatMap(channelFileDir -> {
                    File[] files = channelFileDir.listFiles();
                    if (files != null && files.length > 0) {
                        return Single.just(Okio.buffer(Okio.source(files[0])).readByteString().string(Charset.forName("utf-8")));
                    } else {
                        return Single.error(new FileNotFoundException("channel file not found"));
                    }
                })
                .map(s -> mGson.fromJson(s, ChannelSetModel.class))
                // 删除下载的节目源文件
                .doAfterSuccess(channelSetModel -> mFileManager.deleteFolderFile(mFileManager.getDiskCacheDir(DiskCacheName.TMP).getAbsolutePath(), false))
                // 上传语音支持的频道列表
                .doAfterSuccess(channelSetModel -> mApiRepository.updateVoiceChannel())
                // 更新节目源信息
                .doOnSuccess(channelSetModel -> {
                    if (isFullAmount) {
                        fullAmountUpdateChannel(channelSetModel);
                    } else {
                        incrementUpdateChannel(channelSetModel);
                    }
                    mPrefManager.setCurrentChannelVersion(maxVersion);
                })
                .ignoreElement();
    }

    /**
     * 增量更新节目源
     */
    @SuppressLint("CheckResult")
    private void incrementUpdateChannel(ChannelSetModel channelSetModel) {
        // 删->增->改

        /* 分类类型增量 */
        List<ChannelTypeModel> types = channelSetModel.getTypes();
        if (types != null && !types.isEmpty()) {
            Map<Integer, Collection<ChannelTypeModel>> channelTypeMap = Observable.fromIterable(types).toMultimap(ChannelTypeModel::getType).blockingGet();
            if (types.get(ChannelUpdateType.DELETE) != null) {
                mChannelTypeDao.delete(channelTypeMap.get(ChannelUpdateType.DELETE).toArray(new ChannelTypeModel[0]));
            }
            if (types.get(ChannelUpdateType.INSERT) != null) {
                mChannelTypeDao.insertOrUpdateChannelType(channelTypeMap.get(ChannelUpdateType.INSERT).toArray(new ChannelTypeModel[0]));
            }
            if (types.get(ChannelUpdateType.UPDATE) != null) {
                mChannelTypeDao.insertOrUpdateChannelType(channelTypeMap.get(ChannelUpdateType.UPDATE).toArray(new ChannelTypeModel[0]));
            }
        }

        /* 频道节目增量 */
        List<ChannelModel> channels = channelSetModel.getChannels();
        if (channels != null && !channels.isEmpty()) {
            Map<Integer, Collection<ChannelModel>> channelMap = Observable.fromIterable(channels).toMultimap(ChannelModel::getUpdateType).blockingGet();
            if (channelMap.get(ChannelUpdateType.DELETE) != null) {
                Map<Integer, Collection<ChannelModel>> deleteChannelMap = Observable.fromIterable(channelMap.get(ChannelUpdateType.DELETE)).toMultimap(ChannelModel::getItemId).blockingGet();
                for (Map.Entry<Integer, Collection<ChannelModel>> entry : deleteChannelMap.entrySet()) {
                    List<Integer> channelIds = Observable.fromIterable(entry.getValue()).map(ChannelModel::getId).toList().blockingGet();
                    mChannelDao.deleteByIds(channelIds, entry.getKey());
                }
            }
            if (channelMap.get(ChannelUpdateType.INSERT) != null) {
                mChannelDao.insertOrUpdateChannel(channelMap.get(ChannelUpdateType.INSERT).toArray(new ChannelModel[0]));
            }
            if (channelMap.get(ChannelUpdateType.UPDATE) != null) {
                List<Integer> updateChannelIds = Observable.fromIterable(channelMap.get(ChannelUpdateType.UPDATE)).map(ChannelModel::getNum).toList().blockingGet();
                Map<Integer, Collection<ChannelModel>> channelNumMap = Observable.fromIterable(channelMap.get(ChannelUpdateType.UPDATE)).toMultimap(ChannelModel::getNum).blockingGet();
                mChannelDao.queryByNums(updateChannelIds)
                        .toObservable()
                        .flatMap(Observable::fromIterable)
                        .map(dbChannelModel -> {
                            Collection<ChannelModel> channelModels = channelNumMap.get(dbChannelModel.getNum());
                            for (ChannelModel channelModel : channelModels) {
                                if (channelModel.getItemId() == dbChannelModel.getItemId()) {
                                    channelModel.setCid(dbChannelModel.getCid());
                                    return channelModel;
                                }
                            }
                            return dbChannelModel;
                        })
                        .toList()
                        .doOnSuccess(updateChannels -> mChannelDao.insertOrUpdateChannel(updateChannels.toArray(new ChannelModel[0])))
                        .blockingGet();
            }
        }
    }

    /**
     * 全量更新节目源
     */
    private void fullAmountUpdateChannel(ChannelSetModel channelSetModel) {
        // 清空数据库
        mChannelTypeDao.nukeTable();
        mChannelDao.nukeTable();
        // 插入数据库
        mChannelTypeDao.insertOrUpdateChannelType(channelSetModel.getTypes().toArray(new ChannelTypeModel[0]));
        mChannelDao.insertOrUpdateChannel(channelSetModel.getChannels().toArray(new ChannelModel[0]));
    }

    /**
     * 获取购物频道底图
     */
    public String getShopReproduction(int num) {
        String url = null;
        if (mCachedUpdateInfo.isPresent()) {
            ShopReproductionModel shopReproductionModel = mCachedShopReproduction.get().get(num);
            if (shopReproductionModel != null) {
                url = shopReproductionModel.getUrl();
            }
        }
        return url;
    }

    /**
     * 从数据库通过num查询频道
     */
    public Single<ChannelModel> queryChannelByNum(int num) {
        return mChannelDao.queryChannelByNum(num).compose(ReactivexCompat.singleThreadSchedule());
    }

    /**
     * 从数据库查询存在非隐藏的频道
     */
    public Single<Optional<ChannelModel>> queryChannelByNumOpt(int num) {
        return Single.create((SingleOnSubscribe<Optional<ChannelModel>>) emitter ->
                emitter.onSuccess(Optional.ofNullable(mChannelDao.queryChannelByNumNoHidden(num))))
                .compose(ReactivexCompat.singleThreadSchedule());
    }

    /**
     * 从数据库查询第一个频道
     */
    public Single<ChannelModel> queryFirstChannel() {
        return mChannelDao.queryFirstChannel()
                .compose(ReactivexCompat.singleThreadSchedule());
    }

    /**
     * 查询第一个分类
     */
    public Single<ChannelTypeModel> queryFirstChannelType() {
        return mChannelTypeDao.queryFirstChannelType().compose(ReactivexCompat.singleThreadSchedule());
    }

    /**
     * 从数据库通过id查询频道分类
     *
     * @param id     分类id
     * @param offset 偏移量: +-代表偏移的方向
     */
    public Single<ChannelTypeModel> queryChannelType(int id, int offset) {
        return Single.just(mCachedChannelTypes)
                .flatMap(opl -> {
                    if (opl.isPresent()) {
                        return Single.just(opl.get());
                    } else {
                        return getAllChannelType();
                    }
                })
                .doOnSuccess(types -> mCachedChannelTypes = Optional.of(types))
                .map(types -> {
                    int currentIndex = 0;
                    for (int i = 0; i < types.size(); i++) {
                        if (types.get(i).getId() == id) {
                            currentIndex = i;
                            break;
                        }
                    }
                    int index = (currentIndex + offset) % types.size();
                    return types.get(index >= 0 ? index : types.size() + index);
                });
    }

    /**
     * 获取当前所有的频道类型，动态添加收藏分类、省内频道分类以及是否隐藏外省频道
     */
    public Single<List<ChannelTypeModel>> getAllChannelType() {
        return getAllChannelType(false);
    }

    public Single<List<ChannelTypeModel>> getAllChannelType(boolean includeHidden) {
        return Single.zip((includeHidden ? mChannelTypeDao.getAllIncludeHidden() : mChannelTypeDao.getAll()).subscribeOn(Schedulers.io()),
                mChannelDao.countCollectedChannel().subscribeOn(Schedulers.io()),
                // 设置收藏分类
                (channelTypes, collectSize) -> {
                    if (collectSize > 0 && !channelTypes.contains(mCollectChannelType)) {
                        channelTypes.add(0, mCollectChannelType);
                    } else if (collectSize == 0 && channelTypes.contains(mCollectChannelType)) {
                        channelTypes.remove(mCollectChannelType);
                    }
                    return channelTypes;
                })
                // 添加即时频道
                .map(channelTypeModels -> {
                    if (mImmediateChannel != null) {
                        if (channelTypeModels.get(0) == mCollectChannelType) {
                            channelTypeModels.add(1, mImmediateChannelType);
                        } else {
                            channelTypeModels.add(0, mImmediateChannelType);
                        }
                    }
                    return channelTypeModels;
                })
                // 设置省内频道分类
                .map(channelTypeModels -> {
                    HdpApi hdpApi = mPluginLoader.createApi(HdpApi.class);
                    String realRegion = appContext.getResources().getStringArray(R.array.regions)[0];
                    if (hdpApi != null && !TextUtils.isEmpty(hdpApi.getRegion())) {
                        realRegion = hdpApi.getRegion();
                    }
                    String region = TextUtils.isEmpty(mPrefManager.getSelectedRegion()) ? realRegion : mPrefManager.getSelectedRegion();

                    ChannelTypeModel channelTypeModel = Observable.fromIterable(channelTypeModels)
                            .filter(type -> type.getName().contains(region))
                            .firstElement().blockingGet();
                    if (channelTypeModel != null) {
                        channelTypeModels.remove(channelTypeModel);
                        channelTypeModel.setName("省内频道");
                        channelTypeModels.add(mCachedUpdateInfo.get().getChannelInfo().getWithinRegionIndex(), channelTypeModel);
                    }
                    return channelTypeModels;
                })
                .toObservable()
                .flatMap(Observable::fromIterable)
                .filter(channelTypeModel -> {
                    String name = channelTypeModel.getName();
                    return mPrefManager.getRegionChannelVisibility() || !(name.contains("节目") && (!name.contains("数字") && !name.contains("港") && !name.contains("CIBN")));
                })
                .toList();
    }

    /**
     * 从数据库通过分类id查询频道列表
     *
     * @param typeId 分类id
     * @param offset 偏移量: +-代表偏移的方向
     */
    public Single<List<ChannelModel>> queryChannelList(int typeId, int offset) {
        return queryChannelType(typeId, offset)
                .flatMap(type -> getChannelsWithCollectByType(type.getId()))
                .toObservable()
                .flatMap(Observable::fromIterable)
                .map(channelModel -> {
                    if (mCachedChannelMark.isPresent()) {
                        ChannelMarkModel channelMarkModel = mCachedChannelMark.get().get(channelModel.getId());
                        if (channelMarkModel != null) {
                            if (channelMarkModel.getTypeId() == null || channelMarkModel.getTypeId() == channelModel.getItemId()) {
                                channelModel.setMarkUrl(channelMarkModel.getUrl());
                            }
                        }
                    }
                    return channelModel;
                })
                .toList();
    }

    /**
     * 获取包含收藏的分类列表
     */
    public Single<List<ChannelModel>> getChannelsWithCollectByType(int typeId) {
        return getChannelsWithCollectByType(typeId, false);
    }

    /**
     * 通过频道分类ID获取包含收藏的频道列表
     */
    public Single<List<ChannelModel>> getChannelsWithCollectByType(int typeId, boolean includeHidden) {
        if (typeId == mCollectChannelType.getId()) {
            return mChannelDao.getCollectedChannel().compose(ReactivexCompat.singleThreadSchedule());
        } else if (typeId == mImmediateChannelType.getId()) {
            return Single.just(Collections.singletonList(mImmediateChannel));
        } else {
            if (includeHidden) {
                return mChannelDao.getChannelsByTypeIncludeHidden(typeId).compose(ReactivexCompat.singleThreadSchedule());
            } else {
                return mChannelDao.getChannelsByType(typeId).compose(ReactivexCompat.singleThreadSchedule());
            }
        }
    }

    /**
     * 按照频道号搜索频道列表
     */
    public Single<List<ChannelModel>> searchChannelByNum(String num) {
        return mChannelDao.searchChannelByNum(num).compose(ReactivexCompat.singleThreadSchedule());
    }

    /**
     * 按照频道名称搜索频道列表
     */
    public Single<ChannelModel> searchChannelByName(String name) {
        return mChannelDao.searchChannelByName(name).compose(ReactivexCompat.singleThreadSchedule());
    }

    //region 即时频道

    /**
     * 设置即时频道
     */
    public ChannelModel buildImmediateChannel(String url) {
        mImmediateChannel = new ChannelModel();
        mImmediateChannel.setUrls(Collections.singletonList(url));
        mImmediateChannel.setItemId(mImmediateChannelType.getId());
        mImmediateChannel.setName("即时频道");
        mImmediateChannel.setId(IMMEDIATE_CHANNEL_ID);
        mImmediateChannel.setNum(IMMEDIATE_CHANNEL_ID);
        return mImmediateChannel;
    }

    /**
     * 判断是否是即时频道
     */
    public boolean isImmediateChannel(int channelNum) {
        return channelNum == IMMEDIATE_CHANNEL_ID;
    }

    /**
     * 判断是否是即时频道分类
     */
    public boolean isImmediateChannelType(int channelTypeId) {
        return channelTypeId == mImmediateChannelType.getId();
    }

    /**
     * 获取即时频道信息
     */
    public ChannelModel getImmediateChannel() {
        return mImmediateChannel;
    }

    /**
     * 获取屏蔽推荐
     */
    public Recommend getBlockRecommend() {
        if (mCachedUpdateInfo.isPresent()) {
            FlavorRecommend blockRecommends = mCachedUpdateInfo.get().getBlockRecommends();
            return getCurrentFlavorRecommend(blockRecommends);
        }
        return null;
    }
    //endregion

    //region 运营推广

    /**
     * 获取退出推荐
     */
    public Recommend getExitRecommend() {
        if (mCachedUpdateInfo.isPresent()) {
            FlavorRecommend exitRecommends = mCachedUpdateInfo.get().getExitRecommends();
            return getCurrentFlavorRecommend(exitRecommends);
        }
        return null;
    }

    /**
     * 获取当前渠道推荐信息，分为
     * 1. 当前渠道列表
     * 2. 默认推广列表
     */
    private Recommend getCurrentFlavorRecommend(FlavorRecommend flavorRecommend) {
        if (flavorRecommend == null) {
            return null;
        }

        List<SpecialFlavorRecommend> specialFlavors = flavorRecommend.getSpecialFlavor();
        if (specialFlavors != null && !specialFlavors.isEmpty()) {
            for (SpecialFlavorRecommend specialFlavor : specialFlavors) {
                for (String flavor : specialFlavor.getFlavorName()) {
                    if (flavor.equals(BuildConfig.FLAVOR)) {
                        return filterFlavorRecommend(specialFlavor.getRecommend());
                    }
                }
            }
        }

        DefaultFlavorRecommend defaultFlavor = flavorRecommend.getDefaultFlavor();
        if (defaultFlavor != null) {
            return filterFlavorRecommend(defaultFlavor.getRecommend());
        }
        return null;
    }

    /**
     * 过滤推广信息
     * 1. App推广：按照先后循序获取未安装的App
     * 2. 购物频道推广
     *
     * @param recommends 运营推广列表
     * @return 推广信息
     */
    private Recommend filterFlavorRecommend(List<Recommend> recommends) {
        if (recommends == null || recommends.isEmpty()) {
            return null;
        }

        for (Recommend recommend : recommends) {
            if (recommend.getType() == RecommendType.APK) {
                if (!PhoneCompat.isInstallPackage(appContext, recommend.getPackageName())) {
                    return recommend;
                }
            } else if (recommend.getType() == RecommendType.CHANNEL) {
                return recommend;
            }
        }
        // 默认取最后一个
        return recommends.get(recommends.size() - 1);
    }
    //endregion

    /**
     * 插入或更新节目源
     */
    public void saveChannel(ChannelModel channelModel) {
        Completable.create(emitter -> mChannelDao.insertOrUpdateChannel(channelModel)).compose(ReactivexCompat.completableThreadSchedule()).subscribe();
    }

    /**
     * 隐藏或显示频道分类
     */
    public void hiddenOrNotChannelType(ChannelTypeModel channelTypeModel) {
        Single.concat(
                Single.create(emitter -> {
                    // 更新当前分类
                    mChannelTypeDao.insertOrUpdateChannelType(channelTypeModel);
                    emitter.onSuccess(Optional.empty());
                }),
                Single.create(emitter -> {
                    // 显示或隐藏当前分类下载所有节目频道
                    mChannelDao.hiddenOrShowChannelByTypeId(channelTypeModel.getId(), channelTypeModel.isHidden());
                    emitter.onSuccess(Optional.empty());
                }))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * 清除缓存的分类列表
     */
    public void clearChannelTypeCache() {
        this.mCachedChannelTypes = Optional.empty();
    }

    /**
     * 当前当前分类是否是收藏分类
     */
    public boolean isCollectType(int typeId) {
        return mCollectChannelType.getId() == typeId;
    }

    /**
     * 获取疑问帮助提示图片
     */
    public String getHelpImage() {
        if (mCachedUpdateInfo.isPresent()) {
            return mCachedUpdateInfo.get().getApkInfo().getHelpImage();
        }
        return null;
    }

    /**
     * 获取新版本特征提示图片
     */
    public String getFeatureImage() {
        if (mCachedUpdateInfo.isPresent()) {
            return mCachedUpdateInfo.get().getApkInfo().getFeatureImage();
        }
        return null;
    }

}
