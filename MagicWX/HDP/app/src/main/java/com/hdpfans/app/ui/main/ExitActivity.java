package com.hdpfans.app.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.Presenter;
import com.hdpfans.app.model.entity.Recommend;
import com.hdpfans.app.ui.main.presenter.ExitContract;
import com.hdpfans.app.ui.main.presenter.ExitPresenter;
import com.hdpfans.app.utils.GlideApp;
import com.hdpfans.app.utils.PhoneCompat;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

public class ExitActivity extends FrameActivity implements ExitContract.View {

    public static final String RESULT_CHANNEL_NUM = "result_channel_num";

    public static final String RESULT_OPEN_MENU = "result_open_menu";

    public static Intent navigateToExit(@NonNull Context context) {
        return new Intent(context, ExitActivity.class);
    }

    @Presenter
    @Inject
    ExitPresenter presenter;

    @BindView(R.id.txt_version_tips)
    TextView mTxtVersionTips;
    @BindView(R.id.txt_plugin_info)
    TextView mTxtPluginInfo;
    @BindView(R.id.img_recommend)
    ImageView mImgRecommend;
    @BindView(R.id.btn_guide)
    Button mBtnGuide;
    @BindView(R.id.btn_exit)
    Button mBtnExit;
    @BindView(R.id.progress_download)
    ProgressBar mProgressDownload;
    @BindView(R.id.layout_recommend)
    ViewGroup mLayoutRecommend;
    @BindView(R.id.img_recommend_guide)
    ImageView mImgRecommendGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        mBtnExit.requestFocus();
    }

    @Override
    public void showVersionTips(String tips) {
        mTxtVersionTips.setText(tips);
    }

    @Override
    public void showExitRecommend(Recommend exitRecommend) {
        mLayoutRecommend.setVisibility(View.VISIBLE);

        GlideApp.with(this)
                .load(exitRecommend.getImageUrl())
                .error(R.drawable.default_bg)
                .placeholder(R.drawable.default_bg)
                .fallback(R.drawable.default_bg)
                .into(mImgRecommend);
        mBtnGuide.setText(exitRecommend.getTips());
        final AnimationDrawable animationDrawable = (AnimationDrawable) mImgRecommendGuide.getDrawable();
        animationDrawable.start();

        mBtnGuide.setOnClickListener(v -> {
            new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe();
            presenter.onClickGuide();
        });
    }

    @Override
    public void hideExitRecommend() {
        mLayoutRecommend.setVisibility(View.GONE);
    }

    @Override
    public void openChannel(int channelNum) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CHANNEL_NUM, channelNum);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showPluginInfo(String pluginInfo) {
        mTxtPluginInfo.setVisibility(View.VISIBLE);
        mTxtPluginInfo.setText(pluginInfo);
    }

    @Override
    public void showDownloadProgress(int totalSize, int downloadSize) {
        mProgressDownload.setVisibility(View.VISIBLE);
        mProgressDownload.setMax(totalSize);
        mProgressDownload.setProgress(downloadSize);
    }

    @Override
    public void hideDownloadProgress() {
        mProgressDownload.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_show_menu)
    void showMenu() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_OPEN_MENU, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.btn_exit)
    void exit() {
        PhoneCompat.exit(this);
    }
}
