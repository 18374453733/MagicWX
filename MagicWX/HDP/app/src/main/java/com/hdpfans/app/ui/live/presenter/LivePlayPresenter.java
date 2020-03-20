package com.hdpfans.app.ui.live.presenter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Pair;

import com.hdpfans.api.CntvApi;
import com.hdpfans.api.HdpApi;
import com.hdpfans.api.StatisticsApi;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.data.manager.Settings;
import com.hdpfans.app.data.repository.DiySourceRepository;
import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.exception.MediaPlayFailException;
import com.hdpfans.app.exception.NoCopyrightException;
import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.reactivex.DefaultDisposableSingleObserver;
import com.hdpfans.app.utils.CopyrightBlockChecker;
import com.hdpfans.app.utils.Optional;
import com.hdpfans.app.utils.ReactivexCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;
import com.hdplive.jni.UrlDecode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import hdp.player.ApiKeys;
import hdpfans.com.R;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

@ActivityScope
public class LivePlayPresenter extends BasePresenter<LivePlayContract.View> implements LivePlayContract.Presenter {

    @Inject
    HdpRepository mHdpRepository;
    @Inject
    DiySourceRepository mDiySourceRepository;
    @Inject
    PluginLoader mPluginLoader;
    @Inject
    CopyrightBlockChecker mCopyrightBlockChecker;
    @Inject
    PrefManager mPrefManager;

    private HdpApi mHdpApi;
    private CntvApi mCntvApi;

    private ChannelModel mLastChannelModel;
    private ChannelModel mCurrentChannelModel;

    /**
     * 当前播放的源坐标
     */
    private LongSparseArray<Integer> playerRecordingIndex = new LongSparseArray<>();
    /**
     * 频道重试播放次数记录
     */
    private LongSparseArray<Integer> retryTimes = new LongSparseArray<>();

    private Disposable mChannelInfoDisposable;
    private Disposable mOnKeyChannelDisposable;

    /**
     * 记录当前通过数字键盘的按键
     */
    private StringBuilder onKeyCodeNumber = new StringBuilder();

    @Inject
    public LivePlayPresenter() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void loadIjkPlayer() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        this.mHdpApi = mPluginLoader.createApi(HdpApi.class);
        this.mCntvApi = mPluginLoader.createApi(CntvApi.class);

        // 初始化插件
        mPluginLoader.createApi(StatisticsApi.class);

        onParseIntentToPlay(getIntent());

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void disableChannelInfoTips() {
        getView().hideCurrentChannelInfo();
        if (mChannelInfoDisposable != null && !mChannelInfoDisposable.isDisposed()) {
            mChannelInfoDisposable.dispose();
            mChannelInfoDisposable = null;
        }
        getView().hideOnKeyChannel();
        if (mOnKeyChannelDisposable != null && !mOnKeyChannelDisposable.isDisposed()) {
            mOnKeyChannelDisposable.dispose();
            mOnKeyChannelDisposable = null;
        }
    }

    @Override
    public void onParseIntentToPlay(Intent intent) {
        int channelNum = intent.getIntExtra(ApiKeys.INTENT_API_CHANNEL_NUM, -1);
        String channelName = intent.getStringExtra(ApiKeys.INTENT_API_CHANNEL_NAME);
        String immediateUrl = intent.getStringExtra(ApiKeys.INTENT_API_CHANNEL_URL);
        boolean isSwitchLastChannel = intent.getBooleanExtra(ApiKeys.INTENT_API_LAST_CHANNEL, false);
        boolean isSwitchNextChannel = intent.getBooleanExtra(ApiKeys.INTENT_API_NEXT_CHANNEL, false);

        if (!TextUtils.isEmpty(channelName)) {
            mHdpRepository.searchChannelByName(channelName)
                    .compose(doResolveChannel())
                    .subscribe(new PlayVideoSingleObserver());
        } else if (!TextUtils.isEmpty(immediateUrl)) {
            ChannelModel immediateChannel = mHdpRepository.buildImmediateChannel(immediateUrl);
            switchChannel(immediateChannel);
        } else if (mCurrentChannelModel != null && (isSwitchLastChannel || isSwitchNextChannel)) {
            if (isSwitchLastChannel) {
                switchBeforeChannel();
            } else {
                switchAfterChannel();
            }
        } else {
            Single.just(channelNum)
                    .flatMap(num -> {
                        if (num == -1) {
                            return mHdpRepository.getBootChannel();
                        } else {
                            return Single.just(num);
                        }
                    })
                    .flatMap(bootChannel -> {
                        if (bootChannel == 0) {
                            return mHdpRepository.queryFirstChannel();
                        } else if (mHdpRepository.isImmediateChannel(bootChannel)) {
                            return Single.just(mHdpRepository.getImmediateChannel());
                        } else {
                            return mHdpRepository.queryChannelByNumOpt(bootChannel)
                                    .flatMap(opt -> {
                                        if (opt.isPresent()) {
                                            return Single.just(opt.get());
                                        } else {
                                            return mHdpRepository.queryFirstChannel();
                                        }
                                    });
                        }
                    })
                    .compose(doResolveChannel())
                    .subscribe(new PlayVideoSingleObserver());
        }
    }

    @Override
    public void onClickShowChannelList() {
        if (mCurrentChannelModel != null) {
            getView().navigateToChannelList(this.mCurrentChannelModel);
        }
    }

    @Override
    public void switchChannel(ChannelModel channelModel) {
        if (channelModel == null) return;

        Single.just(channelModel)
                .compose(doResolveChannel())
                .subscribe(new PlayVideoSingleObserver());
    }

    @Override
    public void switchChannel(int channelNum) {
        if (channelNum <= 0) return;

        if (mHdpRepository.isImmediateChannel(channelNum)) {
            switchChannel(mHdpRepository.getImmediateChannel());
        } else {
            mHdpRepository.queryChannelByNum(channelNum)
                    .compose(doResolveChannel())
                    .subscribe(new PlayVideoSingleObserver());
        }
    }

    @Override
    public void switchChannelNextSource() {
        switchChannelNextSource(true);
    }

    @Override
    public void switchChannelNextSource(boolean record) {
        Integer index = playerRecordingIndex.get(mCurrentChannelModel.getNum(), 0);
        if (index >= mCurrentChannelModel.getUrls().size()) {
            index = 0;
        } else {
            index++;
        }
        playerRecordingIndex.put(mCurrentChannelModel.getNum(), index);
        Single.just(mCurrentChannelModel)
                .doOnSuccess(channelModel -> {
                    if (record) {
                        retryTimes.put(channelModel.getNum(), retryTimes.get(channelModel.getNum()) + 1);
                    }
                })
                .compose(doResolveChannel())
                .subscribe(new PlayVideoSingleObserver());
    }

    @Override
    public void switchAfterChannel() {
        if (mCurrentChannelModel == null) return;
        mHdpRepository.queryChannelList(mCurrentChannelModel.getItemId(), 0)
                .flatMap(channelModels -> {
                    // 获取当前频道的坐标
                    int currentIndex = 0;
                    for (int i = 0; i < channelModels.size(); i++) {
                        if (channelModels.get(i).getId() == mCurrentChannelModel.getId()) {
                            currentIndex = i;
                            break;
                        }
                    }

                    if (currentIndex == channelModels.size() - 1) {
                        return mHdpRepository.queryChannelList(mCurrentChannelModel.getItemId(), 1)
                                .toObservable()
                                .flatMap(Observable::fromIterable)
                                .firstElement()
                                .toSingle();
                    } else {
                        return Single.just(channelModels.get(currentIndex + 1));
                    }
                })
                .doOnSuccess(channelModel -> retryTimes.put(channelModel.getNum(), 0))
                .compose(doResolveChannel())
                .subscribe(new PlayVideoSingleObserver());
    }


    @Override
    public void switchBeforeChannel() {
        if (mCurrentChannelModel == null) return;
        mHdpRepository.queryChannelList(mCurrentChannelModel.getItemId(), 0)
                .flatMap(channelModels -> {
                    // 获取当前频道的坐标
                    int currentIndex = 0;
                    for (int i = 0; i < channelModels.size(); i++) {
                        if (channelModels.get(i).getId() == mCurrentChannelModel.getId()) {
                            currentIndex = i;
                            break;
                        }
                    }

                    // 如果等于0，则获取前一个类型的节目
                    if (currentIndex == 0) {
                        return mHdpRepository.queryChannelList(mCurrentChannelModel.getItemId(), -1)
                                .toObservable()
                                .flatMap(Observable::fromIterable)
                                .lastElement()
                                .toSingle();
                    } else {
                        return Single.just(channelModels.get(currentIndex - 1));
                    }
                })
                .doOnSuccess(channelModel -> retryTimes.put(channelModel.getNum(), 0))
                .compose(doResolveChannel())
                .subscribe(new PlayVideoSingleObserver());
    }

    @Override
    public void showChannelSourceList() {
        if (mCurrentChannelModel != null && !mCopyrightBlockChecker.isInBlockTime(mCurrentChannelModel.getNum())) {
            getView().navigateToChannelSourceList(mCurrentChannelModel, playerRecordingIndex.get(mCurrentChannelModel.getNum(), 0));
        }
    }

    @Override
    public void switchChannelSource(int index) {
        playerRecordingIndex.put(mCurrentChannelModel.getNum(), index);
        retryTimes.put(mCurrentChannelModel.getNum(), 0);
        Single.just(mCurrentChannelModel)
                .doOnSuccess(channelModel -> retryTimes.put(channelModel.getNum(), 0))
                .compose(doResolveChannel())
                .subscribe(new PlayVideoSingleObserver());
    }

    @Override
    public void reloadChannel() {
        switchChannel(mCurrentChannelModel);
    }

    @Override
    public void onKeyChannelByNumber(int i) {
        if (onKeyCodeNumber.length() > 6) {
            onKeyCodeNumber = new StringBuilder();
        }
        // 判断搜索第一个字符是否为0
        if (onKeyCodeNumber.length() == 0 && i == 0) {
            if (mLastChannelModel != null) {
                switchChannel(mLastChannelModel);
            }
            return;
        }
        onKeyCodeNumber.append(i);
        if (mOnKeyChannelDisposable != null && !mOnKeyChannelDisposable.isDisposed()) {
            mOnKeyChannelDisposable.dispose();
        }
        mOnKeyChannelDisposable = mHdpRepository.searchChannelByNum(onKeyCodeNumber.toString())
                .toObservable()
                .flatMap(Observable::fromIterable)
                .distinct(ChannelModel::getName)
                .toList()
                .doOnSuccess(channelModels -> {
                    if (!channelModels.isEmpty()) {
                        getView().showOnKeyChannels(onKeyCodeNumber.toString(), channelModels);
                    } else {
                        getView().hideOnKeyChannel();
                    }
                })
                .delay(2, TimeUnit.SECONDS)
                .compose(ReactivexCompat.singleThreadSchedule())
                .subscribe(channelModels -> {
                    getView().hideOnKeyChannel();
                    onKeyCodeNumber = new StringBuilder();
                    if (!channelModels.isEmpty()) {
                        switchChannel(channelModels.get(0));
                    }
                });
    }

    @Override
    public boolean isFromApiCall() {
        return (getIntent().getBooleanExtra(ApiKeys.INTENT_API_HIDE_EXIT, false)
                || getIntent().getBooleanExtra(ApiKeys.INTENT_API_HIDE_LOADING_IMAGE, false));
    }

    @Override
    public void doVoiceCommand(String command) {
        if (TextUtils.isEmpty(command)) {
            return;
        }
        if (mPrefManager.isOpenVoiceSupport()) {
            if (command.equals(getApplicationContext().getResources().getString(R.string.voice_type_back))) {
                getView().backPress();
            } else if (command.equals(getApplicationContext().getResources().getString(R.string.voice_type_last_channel))) {
                switchBeforeChannel();
            } else if (command.equals(getApplicationContext().getResources().getString(R.string.voice_type_next_channel))) {
                switchAfterChannel();
            } else if (command.equals(getApplicationContext().getResources().getString(R.string.voice_type_next_source))) {
                switchChannelNextSource(false);
            }
        } else {
            getView().tipsOpenVoiceSupport();
        }
    }

    /**
     * 节目播放
     */
    private class PlayVideoSingleObserver extends DefaultDisposableSingleObserver<Pair<String, Map<String, String>>> {
        @Override
        public void onSuccess(Pair<String, Map<String, String>> pair) {
            super.onSuccess(pair);
            disableChannelInfoTips();

            String playUrl = pair.first;

            if (TextUtils.isEmpty(playUrl)) {
                switchChannelNextSource();
            } else {
                mPrefManager.setRrecentlyPlayedChannelId(mCurrentChannelModel.getId());
                mCopyrightBlockChecker.setChannelNum(mCurrentChannelModel.getNum());
                // 区分cntv m3u8协议
                Settings settings = new Settings(getApplicationContext());
                if (mCntvApi.isCntvKooUrl(playUrl)) {
                    settings.setPlayer(Settings.PV_PLAYER__IjkKooMediaPlayer);
                    getView().playKooVideo(playUrl, mCntvApi.getKooDrmInfo(playUrl), mCopyrightBlockChecker);
                } else {
                    settings.setPlayer(Settings.PV_PLAYER__IjkMediaPlayer);
                    getView().playVideo(playUrl, pair.second, mCopyrightBlockChecker);
                }
                getView().showShopReproduction(mHdpRepository.getShopReproduction(mCurrentChannelModel.getId()));

                mChannelInfoDisposable = Single.timer(2, TimeUnit.SECONDS)
                        .flatMap(i -> {
                            if (mHdpApi != null && mPrefManager.isOpenChannelEpg()) {
                                return Single.just(Optional.ofNullable(mHdpApi.getCurrentEpgWithNext(mCurrentChannelModel.getEpgId())));
                            } else {
                                return Single.just(Optional.empty());
                            }
                        })
                        .compose(ReactivexCompat.singleTransform())
                        .subscribe(epg -> getView().showCurrentChannelInfo(mCurrentChannelModel, (Optional<Pair<String, String>>) epg));
            }
        }

        @Override
        public void onError(Throwable e) {
            disableChannelInfoTips();
            if (e instanceof MediaPlayFailException) {
                getView().showMediaPlayFail();
            } else if (e instanceof NoCopyrightException) {
                getView().showNoCopyright();
            } else {
                super.onError(e);
            }
        }
    }

    /**
     * 节目源检查和解析
     * <p>
     * 1. 检查版权
     * 2. 检查重拾次数
     * 3. 解密节目源地址
     * 4. 获取源播放地址
     */
    private SingleTransformer<ChannelModel, Pair<String, Map<String, String>>> doResolveChannel() {
        return upstream -> upstream
                .compose(getView().getLifecycleProvider().bindToLifecycle())
                .doOnSuccess(channelModel -> {
                    if (mHdpApi != null) {
                        mHdpApi.stopResolveUrl();
                    }
                })
                .doOnSuccess(channelModel -> {
                    this.mLastChannelModel = mCurrentChannelModel;
                    this.mCurrentChannelModel = channelModel;
                })
                .flatMap(channelModel -> {
                    if (mHdpApi == null) {
                        return Single.error(new MediaPlayFailException());
                    } else {
                        return Single.just(channelModel);
                    }
                })
                // 检查版权
                .flatMap(channelModel -> {
                    if (mCopyrightBlockChecker.isInBlockTime(channelModel.getNum())) {
                        return Single.error(new NoCopyrightException());
                    } else {
                        return Single.just(channelModel);
                    }
                })
                // 解密节目源地址
                .map(channelModel -> {
                    if (channelModel.getUrls() == null) {
                        String[] urlArr = channelModel.getTmpUrls().split("#");
                        List<String> urls = new ArrayList<>(urlArr.length);
                        for (String url : urlArr) {
                            if (!mDiySourceRepository.isDiyType(channelModel.getItemId())) {
                                urls.add(UrlDecode.decode(getApplicationContext(), url));
                            } else {
                                urls.add(url);
                            }
                        }
                        channelModel.setUrls(mHdpApi.parseChannelSourceList(channelModel.getNum(), urls));
                    }
                    return channelModel;
                })
                // 检查具有版权的源
                .map(channelModel -> {
                    List<String> blockChannelSource = mCopyrightBlockChecker.getBlockChannelSource(channelModel);
                    if (blockChannelSource != null) {
                        channelModel.getUrls().removeAll(blockChannelSource);
                    }
                    return channelModel;
                })
                // 检测重试次数
                .flatMap(channelModel -> {
                    Integer retryTime = this.retryTimes.get(channelModel.getNum());
                    System.out.println(channelModel.getNum() + "-->" + retryTime);
                    if (retryTime == null) {
                        retryTime = 0;
                        retryTimes.put(channelModel.getNum(), retryTime);
                    }
                    if (retryTime > channelModel.getUrls().size()) {
                        // 清空记录
                        retryTimes.put(channelModel.getNum(), 0);
                        playerRecordingIndex.put(channelModel.getNum(), 0);
                        return Single.error(new MediaPlayFailException());
                    } else {
                        return Single.just(channelModel);
                    }
                })
                // 获取播放地址
                .flatMap(channelModel ->
                        Single.just(channelModel)
                                .map(c -> mHdpApi.getOriginalUrl(channelModel.getNum(), getCurrentChannelSource(channelModel)))
                                .compose(ReactivexCompat.singleThreadSchedule())
                );
    }

    /**
     * 获取当前播放的节目地址
     */
    private String getCurrentChannelSource(ChannelModel channelModel) {
        // get current index
        Integer index = playerRecordingIndex.get(channelModel.getNum());
        if (index == null) {
            index = 0;
            playerRecordingIndex.put(channelModel.getNum(), 0);
        }
        return channelModel.getUrls().get(index % channelModel.getUrls().size());
    }

    public ChannelModel getCurrentChannelModel() {
        return mCurrentChannelModel;
    }

    public LongSparseArray<Integer> getPlayerRecordingIndex() {
        return playerRecordingIndex;
    }
}
