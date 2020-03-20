package com.hdpfans.app.ui.live;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.Presenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.Recommend;
import com.hdpfans.app.reactivex.DefaultDisposableObserver;
import com.hdpfans.app.ui.live.adapter.ChannelListAdapter;
import com.hdpfans.app.ui.live.presenter.ChannelListContract;
import com.hdpfans.app.ui.live.presenter.ChannelListPresenter;
import com.hdpfans.app.ui.widget.media.FocusKeepRecyclerView;
import com.hdpfans.app.utils.GlideApp;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.BuildConfig;
import hdpfans.com.R;

public class ChannelListActivity extends FrameActivity implements ChannelListContract.View {

    public static final String INTENT_PARAMS_CHANNEL = "intent_params_channel";

    public static final String RESULT_CHANNEL = "RESULT_CHANNEL";

    private LinearLayoutManager layoutManager;
    private View viewByPosition;

    public static Intent navigateToChannelList(Context context, ChannelModel channelModel) {
        Intent intent = new Intent(context, ChannelListActivity.class);
        intent.putExtra(INTENT_PARAMS_CHANNEL, channelModel);
        return intent;
    }

    @Presenter
    @Inject
    ChannelListPresenter presenter;
    @Inject
    ChannelListAdapter mChannelListAdapter;

    @BindView(R.id.txt_channel_type)
    TextView mTxtChannelType;
    @BindView(R.id.recycler_channel_list)
    FocusKeepRecyclerView mRecyclerChannelList;
    @BindView(R.id.layout_channel_info)
    ViewGroup mLayoutChannelInfo;
    @BindView(R.id.layout_copyright_recommend)
    ViewGroup mLayoutCopyrightRecommend;
    @BindView(R.id.img_recommend_icon)
    ImageView mImgRecommendIcon;
    @BindView(R.id.txt_recommend_tips)
    TextView mTxtRecommendTips;
    @BindView(R.id.txt_tips)
    TextView mTxtTips;


    @BindView(R.id.btn_recommend_install)
    Button mBtnRecInstall;
    @BindView(R.id.progress_download)
    ProgressBar mProgressDownload;
    @BindView(R.id.txt_percent)
    TextView mTxtPercent;

    @BindView(R.id.txt_time)
    TextView mTxtTime;
    @BindView(R.id.txt_version_info)
    TextView mTxtVersionInfo;

    private int itemViewHeight;
    private View itemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerChannelList.setLayoutManager(layoutManager);

        mRecyclerChannelList.setHasFixedSize(true);
        mRecyclerChannelList.setAdapter(mChannelListAdapter);
        mRecyclerChannelList.setFocusLostListener((lastFocusChild, direction) -> viewByPosition = lastFocusChild);
        mChannelListAdapter.getOnClickChannelPublishSubject()
                .subscribe(new DefaultDisposableObserver<ChannelModel>() {
                    @Override
                    public void onNext(ChannelModel channelModel) {
                        super.onNext(channelModel);
                        presenter.checkedChannel(channelModel);
                    }
                });
        mChannelListAdapter.getOnLongClickChannelModelPublishSubject()
                .subscribe(new DefaultDisposableObserver<ChannelModel>() {
                    @Override
                    public void onNext(ChannelModel channelModel) {
                        super.onNext(channelModel);
                        presenter.onLongClockChannel(channelModel);
                    }
                });
        mChannelListAdapter.getOnSwitchAfterChannelsPublishSubject()
                .subscribe(new DefaultDisposableObserver<ChannelModel>() {
                    @Override
                    public void onNext(ChannelModel channelModel) {
                        super.onNext(channelModel);
                        presenter.loadAlterChannelListFirstChannel();
                    }
                });
        mChannelListAdapter.getOnSwitchNextChannelsPublishSubject()
                .subscribe(new DefaultDisposableObserver<ChannelModel>() {
                    @Override
                    public void onNext(ChannelModel channelModel) {
                        super.onNext(channelModel);
                        presenter.loadBeforeChannelListLastChannel();
                    }
                });
        mRecyclerChannelList.setOnTouchListener((v, event) -> {
            autoDismiss();
            return false;
        });

        mTxtVersionInfo.setText(getString(R.string.txt_version_info, BuildConfig.VERSION_NAME));
        refreshTime();

        // 点击空白处关闭
        findViewById(android.R.id.content).setOnClickListener(v -> finish());
        itemView = mChannelListAdapter.createItemView(findViewById(android.R.id.content));

        autoDismiss();
    }

    private void autoDismiss() {
        getSafetyHandler().removeCallbacksAndMessages(null);
        getSafetyHandler().postDelayed(() -> {
            if (!mLayoutCopyrightRecommend.isShown()) {
                onBackPressed();
            }
        }, 5 * 1000);
    }

    @OnClick(R.id.btn_switch_left)
    void onClickSwitchLeftChannel() {
        mLayoutCopyrightRecommend.setVisibility(View.GONE);
        presenter.loadBeforeChannelList();
    }

    @OnClick(R.id.btn_switch_right)
    void onClickSwitchRightChannel() {
        mLayoutCopyrightRecommend.setVisibility(View.GONE);
        presenter.loadAlterChannelList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mLayoutCopyrightRecommend.isShown()) {
                    if (viewByPosition != null)
                        viewByPosition.requestFocus();
                } else {
                    presenter.loadBeforeChannelList();
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !mLayoutCopyrightRecommend.isShown()) {
                presenter.loadAlterChannelList();
            }
            if (mLayoutCopyrightRecommend.isShown()
                    && (!mLayoutCopyrightRecommend.isFocused() && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT)) {
                mLayoutCopyrightRecommend.setVisibility(View.GONE);
            }
        }
        autoDismiss();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showChannelType(String name) {
        mTxtChannelType.setText(name);
    }

    @Override
    public void showChannelList(List<ChannelModel> channelList, int defaultNum) {
        int position = 0;
        if (channelList != null && !channelList.isEmpty()) {
            for (int i = 0; i < channelList.size(); i++) {
                if (channelList.get(i).getNum() == defaultNum) {
                    position = i;
                    break;
                }
            }
        }

        mChannelListAdapter.setChannelList(channelList, position);
        int height = layoutManager.getHeight() / 2;
        if (height != 0) {
            layoutManager.scrollToPositionWithOffset(position, layoutManager.getHeight() / 2 - getItemViewHeight() / 2);
        } else {
            int finalPosition = position;
            mRecyclerChannelList.post(() -> layoutManager.scrollToPositionWithOffset(finalPosition, layoutManager.getHeight() / 2 - getItemViewHeight() / 2));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSafetyHandler().removeCallbacksAndMessages(null);
    }

    private int getItemViewHeight() {
        if (itemViewHeight == 0) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            itemViewHeight = itemView.getMeasuredHeight();
        }
        return itemViewHeight;
    }

    @Override
    public void resultChannel(ChannelModel channel) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CHANNEL, channel);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showCopyrightRecommend(Recommend recommend, String btnText) {
        GlideApp.with(this).load(recommend.getImageUrl()).into(mImgRecommendIcon);
        mTxtRecommendTips.setText(recommend.getTips());
        mLayoutCopyrightRecommend.setVisibility(View.VISIBLE);

        mBtnRecInstall.setText(btnText);
    }

    @Override
    public void refreshTime() {
        mTxtTime.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
    }

    @Override
    public void setTips(String s) {
        mTxtTips.setText(s);
    }

    @Override
    public void navigateToApkByPackage(String packageName) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        startActivity(intent);
        finish();
    }

    @Override
    public void showDownloadProgress(int totalSize, int downloadSize, String percent) {
        mBtnRecInstall.setVisibility(View.GONE);
        mProgressDownload.setVisibility(View.VISIBLE);
        mTxtPercent.setVisibility(View.VISIBLE);

        mProgressDownload.setMax(totalSize);
        mProgressDownload.setProgress(downloadSize);
        mTxtPercent.setText(percent);
    }

    @Override
    public void hideDownloadProgress() {
        mBtnRecInstall.setVisibility(View.VISIBLE);
        mProgressDownload.setVisibility(View.GONE);
        mTxtPercent.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_recommend_install)
    void onClickDownloadRecommend() {
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe();
        presenter.openOrDownloadRecApk();
    }
}
