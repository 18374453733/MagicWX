package com.hdpfans.app.ui.live.presenter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Environment;
import android.webkit.URLUtil;

import com.hdpfans.api.HdpApi;
import com.hdpfans.app.data.repository.DiySourceRepository;
import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.annotation.RecommendType;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;
import com.hdpfans.app.model.entity.Recommend;
import com.hdpfans.app.model.event.InstallApkEvent;
import com.hdpfans.app.reactivex.DefaultDisposableObserver;
import com.hdpfans.app.reactivex.DefaultDisposableSingleObserver;
import com.hdpfans.app.utils.CopyrightBlockChecker;
import com.hdpfans.app.utils.PhoneCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import hdpfans.com.R;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Downloading;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Succeed;

import static com.hdpfans.app.ui.live.ChannelListActivity.INTENT_PARAMS_CHANNEL;

@ActivityScope
public class ChannelListPresenter extends BasePresenter<ChannelListContract.View> implements ChannelListContract.Presenter {

    @Inject
    HdpRepository mHdpRepository;
    @Inject
    DiySourceRepository mDiySourceRepository;
    @Inject
    CopyrightBlockChecker mCopyrightBlockChecker;
    @Inject
    PluginLoader mPluginLoader;

    private int mCurrentItemId = -1;
    private ChannelTypeModel mCurrentSelectedType;
    private ChannelModel mCurrentChannelModel;
    private List<ChannelModel> mCurrentTypeChannels;
    private Recommend mBlockRecommend;

    private Disposable apkDownloadDisposable;

    private int offset = 0;

    private String currentRegionsIndex = "00";

    @Inject
    public ChannelListPresenter() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void initializeChannelList() {
        mCurrentChannelModel = getIntent().getParcelableExtra(INTENT_PARAMS_CHANNEL);
        if (mCurrentChannelModel != null) {
            mCurrentItemId = mCurrentChannelModel.getItemId();
        }

        // 显示当前播放频道的列表
        getChannelListByOffset(offset, false, false);

        // 显示系统日期
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getView().getLifecycleProvider().bindToLifecycle())
                .subscribe(new DefaultDisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        super.onNext(aLong);
                        getView().refreshTime();
                    }
                });

        HdpApi api = mPluginLoader.createApi(HdpApi.class);
        if (api != null && api.getRegion() != null) {
            List<String> regionList = Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.regions));
            int index = regionList.indexOf(api.getRegion());
            this.currentRegionsIndex = new DecimalFormat("00").format(index);
        }
    }

    @Override
    public void loadBeforeChannelList() {
        getChannelListByOffset(offset - 1, false, false);
    }

    @Override
    public void loadBeforeChannelListLastChannel() {
        getChannelListByOffset(offset - 1, true, false);
    }

    @Override
    public void loadAlterChannelList() {
        getChannelListByOffset(offset + 1, false, false);
    }

    @Override
    public void loadAlterChannelListFirstChannel() {
        getChannelListByOffset(offset + 1, false, true);
    }

    @Override
    public void checkedChannel(ChannelModel channelModel) {
        mBlockRecommend = mHdpRepository.getBlockRecommend();
        if (mCopyrightBlockChecker.isInBlockTime(channelModel.getNum())
                && mBlockRecommend != null
                && mBlockRecommend.getType() == RecommendType.APK) {
            getView().showCopyrightRecommend(mBlockRecommend,
                    PhoneCompat.isInstallPackage(getApplicationContext(), mBlockRecommend.getPackageName()) ? "打开" : "安装");
        } else {
            if (mCurrentSelectedType != null && mHdpRepository.isCollectType(mCurrentSelectedType.getId())) {
                channelModel.setItemId(mCurrentSelectedType.getId());
            }
            getView().resultChannel(channelModel);
        }
    }

    /**
     * 原始节目点击收藏和取消收藏
     * 自定源点击删除
     */
    @Override
    public void onLongClockChannel(ChannelModel channelModel) {
        // 不处理即时频道
        if (mHdpRepository.isImmediateChannel(channelModel.getNum())) return;

        if (mDiySourceRepository.isDiyType(channelModel.getItemId())) {
            mDiySourceRepository.delDiyChannel(channelModel);
            mCurrentTypeChannels.remove(channelModel);
        } else {
            channelModel.setCollect(!channelModel.isCollect());
            mHdpRepository.saveChannel(channelModel);
        }
    }

    @Override
    public void openOrDownloadRecApk() {
        if (mBlockRecommend != null && mBlockRecommend.getType() == RecommendType.APK) {
            if (PhoneCompat.isInstallPackage(getApplicationContext(), mBlockRecommend.getPackageName())) {
                getView().navigateToApkByPackage(mBlockRecommend.getPackageName());
            } else {
                downloadApkFile();
            }
        }
    }

    private void downloadApkFile() {
        if (apkDownloadDisposable == null || !apkDownloadDisposable.isDisposed()) {
            Mission mission = new Mission(mBlockRecommend.getDownloadUrl(),
                    URLUtil.guessFileName(mBlockRecommend.getDownloadUrl(), null, null),
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            apkDownloadDisposable = RxDownload.INSTANCE.create(mission, true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(status -> {
                        if (status instanceof Downloading) {
                            getView().showDownloadProgress((int) status.getTotalSize(), (int) status.getDownloadSize(), status.percent());
                        } else if (status instanceof Succeed) {
                            getView().hideDownloadProgress();
                            File apkFile = new File(mission.getSavePath() + File.separator + mission.getSaveName());
                            if (apkFile.exists()) {
                                EventBus.getDefault().post(new InstallApkEvent(apkFile.getAbsolutePath()));
                                apkDownloadDisposable = null;
                            } else {
                                RxDownload.INSTANCE.clear(mission);
                                RxDownload.INSTANCE.delete(mission, false);
                                getView().toast("文件已被删除，请重新下载");
                            }
                        } else if (status instanceof Failed) {
                            getView().hideDownloadProgress();
                            apkDownloadDisposable = null;
                            getView().toast("文件下载失败，请检查权限或网络并重新下载");
                            File file = new File(mBlockRecommend.getDownloadUrl());
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    });
        }
    }

    private void getChannelListByOffset(int toOffset, boolean lastChannel, boolean firstChannel) {
        Single.just(mCurrentItemId)
                .flatMap(id -> {
                    if (id == -1) {
                        return mHdpRepository.queryFirstChannelType().map(ChannelTypeModel::getId);
                    }
                    return Single.just(mCurrentItemId);
                })
                .flatMap(id -> mHdpRepository.queryChannelType(mCurrentItemId, toOffset))
                .doOnSuccess(type -> mCurrentSelectedType = type)
                .doOnSuccess(type -> getView().showChannelType(type.getName()))
                .doOnSuccess(type -> {
                    if (mHdpRepository.isCollectType(type.getId())) {
                        getView().setTips("（长按OK键或菜单键取消收藏）");
                    } else if (mDiySourceRepository.isDiyType(type.getId())) {
                        getView().setTips("（长按OK键或菜单键删除该节目）");
                    } else {
                        getView().setTips("（长按OK键或菜单键收藏该节目）");
                    }
                })
                .flatMap(type -> mHdpRepository.queryChannelList(mCurrentItemId, toOffset))
                .toObservable()
                .flatMap(Observable::fromIterable)
                .map(channelModel -> {
                    List<Integer> blockShowAreaChannelNums = mHdpRepository.getBlockShowAreaChannelNums();
                    if (blockShowAreaChannelNums != null && blockShowAreaChannelNums.contains(channelModel.getNum())) {
                        channelModel.setNumAlias(String.format(Locale.getDefault(), "%d%s", channelModel.getNum(), currentRegionsIndex));
                    }
                    return channelModel;
                })
                .toList()
                .subscribe(new DefaultDisposableSingleObserver<List<ChannelModel>>() {
                    @Override
                    public void onSuccess(List<ChannelModel> channelModels) {
                        super.onSuccess(channelModels);
                        mCurrentTypeChannels = channelModels;

                        int focusedChannelNum = -1;
                        if (mCurrentChannelModel != null) {
                            focusedChannelNum = mCurrentChannelModel.getNum();
                        }
                        if (lastChannel && channelModels != null) {
                            focusedChannelNum = channelModels.get(channelModels.size() - 1).getNum();
                        }
                        if (firstChannel && channelModels != null) {
                            focusedChannelNum = channelModels.get(0).getNum();
                        }

                        getView().showChannelList(mCurrentTypeChannels, focusedChannelNum);
                        offset = toOffset;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void clearChannelTypeCache() {
        mHdpRepository.clearChannelTypeCache();
    }
}
