package com.hdpfans.app.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.Presenter;
import com.hdpfans.app.ui.live.LivePlayActivity;
import com.hdpfans.app.ui.main.presenter.MainContract;
import com.hdpfans.app.ui.main.presenter.MainPresenter;
import com.hdpfans.app.utils.GlideApp;
import com.hdpfans.app.utils.PhoneCompat;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import butterknife.BindView;
import hdp.player.ApiKeys;
import hdpfans.com.R;

public class MainActivity extends FrameActivity implements MainContract.View {

    @Presenter
    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.img_launch)
    ImageView mImgLaunch;
    @BindView(R.id.layout_third_loading)
    ViewGroup mLayoutThirdLoading;

    private ProgressDialog mDownloadProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        askReadPhoneStatusPermission();
    }

    private void askReadPhoneStatusPermission() {
        new RxPermissions(this).requestEach(Manifest.permission.READ_PHONE_STATE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        mMainPresenter.getUpdateConfig();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        askReadPhoneStatusPermission();
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(getString(R.string.txt_need_grant_read_phone_permission, getString(R.string.app_name)))
                                .setNegativeButton(R.string.txt_go_grant, (dialog, which) -> {
                                    try {
                                        startActivity(getAppDetailSettingIntent());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        finish();
                                    }
                                    dialog.dismiss();
                                })
                                .setPositiveButton(R.string.txt_exit, (dialog, which) -> {
                                    PhoneCompat.exit(this);
                                    dialog.dismiss();
                                })
                                .create()
                                .show();
                    }
                });
    }

    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        return localIntent;
    }

    @Override
    public void showNeedUpdateDialog(String title, String message, boolean force) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.txt_update, (dialog, which) -> {
                    askReadExternalStorageToDownload();
                    dialog.dismiss();
                })
                .setCancelable(!force)
                .setNegativeButton(force ? R.string.txt_exit : android.R.string.cancel, (dialog, which) -> {
                    if (force) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } else {
                        dialog.dismiss();
                        navigateToLivePlay();
                    }
                })
                .create()
                .show();
    }

    private void askReadExternalStorageToDownload() {
        new RxPermissions(MainActivity.this)
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        mMainPresenter.downloadNewVersionApk();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        askReadExternalStorageToDownload();
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(getString(R.string.txt_need_grant_write_storage_permission, getString(R.string.app_name)))
                                .setNegativeButton(R.string.txt_go_grant, (dialog, which) -> {
                                    try {
                                        startActivity(getAppDetailSettingIntent());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        finish();
                                    }
                                    dialog.dismiss();
                                })
                                .setPositiveButton(R.string.txt_exit, (dialog, which) -> {
                                    PhoneCompat.exit(this);
                                    dialog.dismiss();
                                })
                                .create()
                                .show();
                    }
                });
    }

    @Override
    public void navigateToLivePlay() {
        Intent intent = new Intent(this, LivePlayActivity.class);
        ApiKeys.copyApiParams(getIntent(), intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLaunchImage(String launchImage) {
        GlideApp.with(this)
                .load(launchImage)
                .placeholder(R.drawable.default_bg)
                .error(R.drawable.default_bg)
                .into(mImgLaunch);

    }

    @Override
    public void showDownloadingProgress(int totalSize, int downloadSize, String percent) {
        if (mDownloadProgressDialog == null) {
            mDownloadProgressDialog = new ProgressDialog(this);
            mDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDownloadProgressDialog.setCancelable(false);
            mDownloadProgressDialog.setTitle("正在下载更新");
            mDownloadProgressDialog.show();
        }
        mDownloadProgressDialog.setMax(totalSize);
        mDownloadProgressDialog.setProgress(downloadSize);
        mDownloadProgressDialog.setMessage(percent);
    }

    @Override
    public void hideDownloadProgress() {
        if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing()) {
            mDownloadProgressDialog.dismiss();
        }
    }

    @Override
    public void showNetworkErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("网络连接失败,是否进行网络设置？")
                .setPositiveButton("设置", (dialog, which) -> {
                    dialog.dismiss();
                    try {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.exit(-1);
                    finish();
                })
                .setCancelable(false)
                .setNegativeButton("退出", (dialog, which) -> {
                    dialog.dismiss();
                    System.exit(-1);
                    finish();
                })
                .create()
                .show();
    }

    @Override
    public void hideLaunchBackground() {
        mLayoutThirdLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
    }
}
