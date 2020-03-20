package com.hdpfans.app.ui.main.presenter;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.github.zafarkhaja.semver.Version;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.annotation.Flavor;
import com.hdpfans.app.model.entity.ApkInfoModel;
import com.hdpfans.app.model.entity.UpdateInfoModel;
import com.hdpfans.app.model.event.InstallApkEvent;
import com.hdpfans.app.reactivex.DefaultDisposableSingleObserver;
import com.hdpfans.app.utils.Optional;
import com.hdpfans.app.utils.ReactivexCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import hdp.player.ApiKeys;
import hdpfans.com.BuildConfig;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Downloading;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Succeed;

@ActivityScope
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private static final String DEFAULT_APK_DOWNLOAD_URL = "%s%s/HDP_%s_%d.apk";

    private static final String DEFAULT_HDP_DOWNLOAD_URL = "%s%s_%d.apk";

    @Inject
    HdpRepository mHdpRepository;
    @Inject
    PrefManager mPrefManager;
    @Inject
    PluginLoader mPluginLoader;

    private String mApkDownloadUrl;
    private boolean mForceUpdate;

    @Inject
    public MainPresenter() {
    }

    @Override
    public void getUpdateConfig() {

        if (getIntent().getBooleanExtra(ApiKeys.INTENT_API_HIDE_LOADING_IMAGE, false)) {
            getView().hideLaunchBackground();
        } else {
            if (!TextUtils.isEmpty(mPrefManager.getLaunchImage())) {
                getView().showLaunchImage(mPrefManager.getLaunchImage());
            }
        }

        mHdpRepository.getUpdateInfo()
                .compose(getView().getLifecycleProvider().bindToLifecycle())
                .flatMap(updateInfoModel -> Single.zip(mHdpRepository.downloadPlugin(updateInfoModel.getPlugins()).toSingleDefault(Optional.empty()),
                        mHdpRepository.updateChannel(updateInfoModel.getChannelInfo()).toSingleDefault(Optional.empty()),
                        (optional, optional2) -> updateInfoModel))
                .compose(ReactivexCompat.singleThreadSchedule())
                .subscribe(new DefaultDisposableSingleObserver<UpdateInfoModel>() {
                    @Override
                    public void onSuccess(UpdateInfoModel updateInfoModel) {
                        super.onSuccess(updateInfoModel);

                        // 检查升级
                        ApkInfoModel apkInfo = updateInfoModel.getApkInfo();
                        if (BuildConfig.VERSION_CODE < apkInfo.getMaxVersion()) {

                            if (Flavor.HDP.equalsIgnoreCase(BuildConfig.FLAVOR)) {
                                mApkDownloadUrl = String.format(Locale.getDefault(), DEFAULT_HDP_DOWNLOAD_URL, apkInfo.getApkUrl(), BuildConfig.FLAVOR.toUpperCase(), apkInfo.getMaxVersion());
                            } else {
                                mApkDownloadUrl = String.format(Locale.getDefault(), DEFAULT_APK_DOWNLOAD_URL, apkInfo.getApkUrl(), BuildConfig.FLAVOR.toUpperCase(), BuildConfig.FLAVOR.toUpperCase(), apkInfo.getMaxVersion());
                            }

                            if (apkInfo.getForceVersions() != null && apkInfo.getForceVersions().size() > 0) {
                                for (String semantic : apkInfo.getForceVersions()) {
                                    if (Version.forIntegers(BuildConfig.VERSION_CODE).satisfies(semantic)) {
                                        mForceUpdate = true;
                                        break;
                                    }
                                }
                            }
                            getView().showNeedUpdateDialog("发现新版本", apkInfo.getUpdateInfo(), mForceUpdate);
                        } else {
                            getView().navigateToLivePlay();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getView().showNetworkErrorDialog();
                    }
                });
    }

    @Override
    public void downloadNewVersionApk() {
        Mission mission = new Mission(mApkDownloadUrl,
                URLUtil.guessFileName(mApkDownloadUrl, null, null),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        getView().showDownloadingProgress(0, 0, "正在获取...");
        RxDownload.INSTANCE.create(mission, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    if (status instanceof Downloading) {
                        getView().showDownloadingProgress((int) status.getTotalSize(), (int) status.getDownloadSize(), status.percent());
                    } else if (status instanceof Succeed) {
                        getView().hideDownloadProgress();
                        File apkFile = new File(mission.getSavePath() + File.separator + mission.getSaveName());
                        if (apkFile.exists()) {
                            EventBus.getDefault().post(new InstallApkEvent(apkFile.getAbsolutePath(), true));
                        } else {
                            RxDownload.INSTANCE.clear(mission);
                            RxDownload.INSTANCE.delete(mission, false);
                            downloadNewVersionApk();
                        }
                    } else if (status instanceof Failed) {
                        getView().hideDownloadProgress();
                        if (!mForceUpdate) {
                            getView().toast("文件下载失败，请检查网络");
                            getView().navigateToLivePlay();
                        } else {
                            getView().showNetworkErrorDialog();
                        }
                        File file = new File(mApkDownloadUrl);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                });
    }
}
