package com.hdpfans.app.ui.main.presenter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Environment;
import android.util.Base64;
import android.webkit.URLUtil;

import com.hdpfans.api.Api;
import com.hdpfans.api.HdpApi;
import com.hdpfans.app.data.repository.DiySourceRepository;
import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.annotation.RecommendType;
import com.hdpfans.app.model.entity.Recommend;
import com.hdpfans.app.model.event.InstallApkEvent;
import com.hdpfans.app.utils.PhoneCompat;
import com.hdpfans.app.utils.plugin.PluginBuildConfig;
import com.hdpfans.app.utils.plugin.PluginLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import hdpfans.com.BuildConfig;
import hdpfans.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Downloading;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Succeed;

/**
 * 退出页面Presenter：
 * 1. 显示系统基本信息
 * 2. 显示运营推广
 * --> 软件下载推广
 * --> 购物频道推广
 */
@ActivityScope
public class ExitPresenter extends BasePresenter<ExitContract.View> implements ExitContract.Presenter {

    @Inject
    HdpRepository mHdpRepository;
    @Inject
    DiySourceRepository mDiySourceRepository;
    @Inject
    PluginLoader mPluginLoader;

    private Recommend exitRecommend;
    private Disposable apkDownloadDisposable;

    @Inject
    public ExitPresenter() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void initializeExitInformation() {
        getView().showVersionTips(getApplicationContext().getString(R.string.txt_version_tips,
                BuildConfig.VERSION_NAME,
                BuildConfig.DEBUG ? BuildConfig.FLAVOR : Base64.encodeToString(BuildConfig.FLAVOR.getBytes(), Base64.NO_PADDING),
                PhoneCompat.getLanIpAddress(getApplicationContext()),
                mDiySourceRepository.getRemotePort()));

        exitRecommend = mHdpRepository.getExitRecommend();
        if (exitRecommend != null) {
            getView().showExitRecommend(exitRecommend);
        } else {
            getView().hideExitRecommend();
        }

        if (BuildConfig.DEBUG) {
            getView().showPluginInfo(getPluginsInfo(Collections.singletonList(HdpApi.class)));
        }
    }

    private <T extends Api> String getPluginsInfo(List<Class<T>> plugins) {
        StringBuilder pluginsStringBuilder = new StringBuilder();
        for (Class<T> plugin : plugins) {
            PluginBuildConfig pluginBuildConfig = mPluginLoader.getPluginBuildConfig(plugin);
            pluginsStringBuilder.append(String.format(Locale.getDefault(), "%s:\n\t%s-%s\n",
                    plugin.getSimpleName(),
                    pluginBuildConfig.getFrom(),
                    pluginBuildConfig.getVersionName()));
        }
        return pluginsStringBuilder.toString();
    }


    @Override
    public void onClickGuide() {
        if (exitRecommend.getType() == RecommendType.CHANNEL) {
            getView().openChannel(exitRecommend.getChannelNum());
        } else if (exitRecommend.getType() == RecommendType.APK) {
            downloadApkFile();
        }
    }

    private void downloadApkFile() {
        if (apkDownloadDisposable == null || !apkDownloadDisposable.isDisposed()) {
            Mission mission = new Mission(exitRecommend.getDownloadUrl(),
                    URLUtil.guessFileName(exitRecommend.getDownloadUrl(), null, null),
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            apkDownloadDisposable = RxDownload.INSTANCE.create(mission, true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(status -> {
                        if (status instanceof Downloading) {
                            getView().showDownloadProgress((int) status.getTotalSize(), (int) status.getDownloadSize());
                        } else if (status instanceof Succeed) {
                            getView().hideDownloadProgress();
                            File apkFile = new File(mission.getSavePath() + File.separator + mission.getSaveName());
                            if (apkFile.exists()) {
                                EventBus.getDefault().post(new InstallApkEvent(apkFile.getAbsolutePath()));
                                apkDownloadDisposable = null;
                            } else {
                                RxDownload.INSTANCE.clear(mission);
                                RxDownload.INSTANCE.delete(mission, false);
                                downloadApkFile();
                            }
                        } else if (status instanceof Failed) {
                            getView().hideDownloadProgress();
                            apkDownloadDisposable = null;
                            getView().toast("文件下载失败，请检查网络并重新下载");
                            File file = new File(exitRecommend.getDownloadUrl());
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    });
        }
    }
}
