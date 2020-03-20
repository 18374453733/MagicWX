package com.hdpfans.app.ui.live;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.Presenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.service.LocalServerService;
import com.hdpfans.app.ui.live.presenter.LivePlayContract;
import com.hdpfans.app.ui.live.presenter.LivePlayPresenter;
import com.hdpfans.app.ui.main.ExitActivity;
import com.hdpfans.app.ui.widget.media.IjkVideoView;
import com.hdpfans.app.utils.CopyrightBlockChecker;
import com.hdpfans.app.utils.GlideApp;
import com.hdpfans.app.utils.Optional;
import com.hdpfans.app.utils.PhoneCompat;
import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;
import com.paster.util.JsonUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import hdpfans.com.BuildConfig;
import hdpfans.com.R;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.hdpfans.app.ui.live.MenuActivity.OPERATING_CODE_CHANNEL_MANAGER;
import static com.hdpfans.app.ui.live.MenuActivity.OPERATING_CODE_EXIT;
import static com.hdpfans.app.ui.live.MenuActivity.OPERATING_CODE_TOGGLE_PLAYER;
import static com.hdpfans.app.ui.live.MenuActivity.OPERATING_CODE_TOGGLE_RATIO;
import static com.hdpfans.app.ui.live.MenuActivity.RESULT_RESULT_OPERATING_CODE;

public class LivePlayActivity extends FrameActivity implements LivePlayContract.View {

    /**
     * 跳转{@link ChannelListActivity}请求码
     */
    private static final int REQUEST_CODE_CHANNEL_LIST = 0x1;

    /**
     * 跳转{@link ChannelSourceListActivity}请求码
     */
    private static final int REQUEST_CODE_CHANNEL_SOURCE_LIST = 0x2;

    /**
     * 跳转{@link ExitActivity}请求码
     */
    private static final int REQUEST_CODE_EXIT = 0x3;

    /**
     * 跳转{@link MenuActivity}请求码
     */
    private static final int REQUEST_CODE_MENU = 0x4;

    /**
     * 本地广播，响应菜单栏操作
     */
    public static final String ACTION_OPERATING_MENU = "action_operating_menu";
    /**
     * 本地广播，播放节目源
     */
    public static final String ACTION_PLAY_API = "action_play_api";

    @Presenter
    @Inject
    LivePlayPresenter presenter;

    @BindView(R.id.view_video)
    IjkVideoView mVideoView;
    @BindView(R.id.hud_view)
    TableLayout mHudView;

    @BindView(R.id.img_shop_reproduction)
    ImageView mImgShopReproduction;
    @BindView(R.id.img_fail)
    ImageView mImgFail;

    @BindView(R.id.layout_channel_info)
    ViewGroup mLayoutChannelInfo;
    @BindView(R.id.txt_channel_name)
    TextView mTxtChannelName;
    @BindView(R.id.txt_channel_num)
    TextView mTxtChannelNum;
    @BindView(R.id.txt_playing_epg)
    TextView mTxtPlayingEpg;
    @BindView(R.id.txt_next_epg)
    TextView mTxtNextEpg;

    @BindView(R.id.txt_on_key_channel)
    TextView mTxtOnKeyChannel;
    @BindView(R.id.progress_video_loading)
    ProgressBar mProgressVideoLoading;

    private Scene mVoiceScene;
    private Feedback mVoiceFeedback;

    private String mShopShopReproductionUrl;

    private Handler mPlayVideoHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, LocalServerService.class));
        } else {
            startService(new Intent(this, LocalServerService.class));
        }

        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe();

        // register broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(menuBroadcastReceiver, new IntentFilter(ACTION_OPERATING_MENU));
        LocalBroadcastManager.getInstance(this).registerReceiver(playChannelReceiver, new IntentFilter(ACTION_PLAY_API));

        if (BuildConfig.DEBUG) {
            mHudView.setVisibility(View.VISIBLE);
            mVideoView.setHudView(mHudView);
        }
        mVideoView.setOnErrorListener(onErrorListener);
        mVideoView.setOnPreparedListener(onPreparedListener);
        mVideoView.setOnCompletionListener(onCompletionListener);
        mVideoView.setOnInfoListener(onInfoListener);
        mVideoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getY() > v.getHeight() / 4 * 3) {
                    presenter.showChannelSourceList();
                } else if (event.getX() < v.getWidth() / 2) {
                    presenter.onClickShowChannelList();
                } else {
                    navigateToMenuActivity();
                }
                return true;
            }
            return false;
        });

        mVoiceScene = new Scene(this);
        mVoiceScene.init(new VoiceSceneListener());
        mVoiceFeedback = new Feedback(this);
    }

    //region media player callback
    private IjkMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
            if (extra == IjkVideoView.MEDIA_ERROR_BLOCK) {
                showNoCopyright();
            } else {
                presenter.switchChannelNextSource();
            }
            return true;
        }
    };

    private IjkMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            presenter.reloadChannel();
        }
    };

    private IjkMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if (mImgFail.isShown()) {
                mImgFail.setVisibility(View.GONE);
            }

        }
    };

    private IjkMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    showVideoLoading();
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    hideVideoLoading();
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    GlideApp.with(LivePlayActivity.this).load(mShopShopReproductionUrl).into(mImgShopReproduction);
                    hideVideoLoading();
                    break;
            }
            return false;
        }
    };
    //endregion

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.reloadChannel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        presenter.onParseIntentToPlay(intent);
    }

    //region Local Broadcast Receiver
    private BroadcastReceiver menuBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            parseMenuOperating(intent);
        }
    };

    private BroadcastReceiver playChannelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.onParseIntentToPlay(intent);
        }
    };
    //endregion

    public void navigateToMenuActivity() {
        startActivityForResult(new Intent(this, MenuActivity.class), REQUEST_CODE_MENU);
    }

    @Override
    public void onBackPressed() {
        if (presenter.isFromApiCall()) {
            PhoneCompat.exit(this);
        } else {
            startActivityForResult(ExitActivity.navigateToExit(this), REQUEST_CODE_EXIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void playVideo(String url, Map<String, String> header, CopyrightBlockChecker copyrightBlockChecker) {
        mPlayVideoHandler.removeCallbacksAndMessages(null);
        showVideoLoading();
        mPlayVideoHandler.postDelayed(() -> {
            mVideoView.setBlockChecker(copyrightBlockChecker);
            mVideoView.setVideoURI(Uri.parse(url), header);
            mVideoView.start();
        }, 300);
    }

    private boolean isFirstLoadKoo = true;

    @Override
    public void playKooVideo(String url, String[] drmInfo, CopyrightBlockChecker copyrightBlockChecker) {
        mPlayVideoHandler.removeCallbacksAndMessages(null);
        showVideoLoading();
        mPlayVideoHandler.postDelayed(() -> {
            mVideoView.setBlockChecker(copyrightBlockChecker);
            mVideoView.setKooVideoURI(Uri.parse(url), drmInfo);
            mVideoView.start();
            // FIXME: kooplayer第一次必须调用两次
            if (isFirstLoadKoo) {
                mVideoView.setKooVideoURI(Uri.parse(url), drmInfo);
                mVideoView.start();
                isFirstLoadKoo = false;
            }
        }, 300);
    }

    @Override
    public void navigateToChannelList(ChannelModel channelModel) {
        startActivityForResult(ChannelListActivity.navigateToChannelList(this, channelModel), REQUEST_CODE_CHANNEL_LIST);
    }

    @Override
    public void showShopReproduction(String url) {
        this.mShopShopReproductionUrl = url;
    }

    @Override
    public void showMediaPlayFail() {
        hideVideoLoading();
        mImgFail.setVisibility(View.VISIBLE);
        GlideApp.with(this).load(R.drawable.bg_no_signal).into(mImgFail);
        mVideoView.stopPlayback();
    }

    @Override
    public void showNoCopyright() {
        hideVideoLoading();
        mImgFail.setVisibility(View.VISIBLE);
        GlideApp.with(this).load(R.drawable.bg_no_copyright).into(mImgFail);
        mVideoView.stopPlayback();
    }

    @Override
    public void navigateToChannelSourceList(ChannelModel currentChannelModel, int index) {
        startActivityForResult(ChannelSourceListActivity.navigateToChannelSourceList(this, currentChannelModel, index), REQUEST_CODE_CHANNEL_SOURCE_LIST);
    }

    @Override
    public void showCurrentChannelInfo(ChannelModel channelModel, Optional<Pair<String, String>> epg) {
        mLayoutChannelInfo.setVisibility(View.VISIBLE);
        mTxtChannelNum.setText(String.valueOf(channelModel.getNum()));
        mTxtChannelName.setText(channelModel.getName());

        if (epg.isPresent()) {
            // 显示当前节目信息
            SpannableString playingEpgSpannable = new SpannableString(getString(R.string.txt_playing_epg, epg.get().first));
            playingEpgSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.playing_epg)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTxtPlayingEpg.setText(playingEpgSpannable);
            mTxtPlayingEpg.setVisibility(View.VISIBLE);
            mTxtNextEpg.setVisibility(View.GONE);
            // 显示下一个节目信息
            getSafetyHandler().postDelayed(() -> {
                mTxtPlayingEpg.setVisibility(View.GONE);
                mTxtNextEpg.setVisibility(View.VISIBLE);
                SpannableString nextEpgSpannable = new SpannableString(getString(R.string.txt_next_epg, epg.isPresent() ? epg.get().second : "节目以实时播出为准"));
                nextEpgSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.next_epg)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTxtNextEpg.setText(nextEpgSpannable);
            }, 2 * 1000);
            getSafetyHandler().postDelayed(this::hideCurrentChannelInfo, 5 * 1000);
        } else {
            getSafetyHandler().postDelayed(this::hideCurrentChannelInfo, 2 * 1000);
        }
    }

    @Override
    public void hideCurrentChannelInfo() {
        getSafetyHandler().removeCallbacksAndMessages(null);
        mLayoutChannelInfo.setVisibility(View.GONE);
    }

    @Override
    public void showOnKeyChannels(String keyNum, List<ChannelModel> channelModels) {
        hideCurrentChannelInfo();
        mTxtOnKeyChannel.setVisibility(View.VISIBLE);
        StringBuilder channelInfoBuilder = new StringBuilder();
        channelInfoBuilder.append("<big><strong>").append(keyNum).append("</strong></big>");
        for (int i = 0; i < channelModels.size() && i < 5; i++) {
            ChannelModel channelModel = channelModels.get(i);
            channelInfoBuilder.append("<br>").append(channelModel.getNum()).append(" ").append(channelModel.getName());
        }
        mTxtOnKeyChannel.setText(Html.fromHtml(channelInfoBuilder.toString()));
    }

    @Override
    public void hideOnKeyChannel() {
        if (mTxtOnKeyChannel.isShown()) {
            mTxtOnKeyChannel.setVisibility(View.GONE);
            mTxtOnKeyChannel.setText(null);
        }
    }

    @Override
    public void backPress() {
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //  数字按键
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            presenter.onKeyChannelByNumber(keyCode - KeyEvent.KEYCODE_0);
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                presenter.onClickShowChannelList();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                presenter.switchAfterChannel();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                presenter.switchBeforeChannel();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                presenter.showChannelSourceList();
                return true;
            case KeyEvent.KEYCODE_MENU:
                navigateToMenuActivity();
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPlayVideoHandler.removeCallbacksAndMessages(null);
        getSafetyHandler().removeCallbacksAndMessages(null);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(menuBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playChannelReceiver);

        mVoiceScene.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHANNEL_LIST:
                    ChannelModel channelModel = data.getParcelableExtra(ChannelListActivity.RESULT_CHANNEL);
                    presenter.switchChannel(channelModel);
                    return;
                case REQUEST_CODE_CHANNEL_SOURCE_LIST:
                    int index = data.getIntExtra(ChannelSourceListActivity.RESULT_CHANNEL_INDEX, -1);
                    presenter.switchChannelSource(index);
                    return;
                case REQUEST_CODE_EXIT:
                    if (data.getBooleanExtra(ExitActivity.RESULT_OPEN_MENU, false)) {
                        startActivityForResult(new Intent(this, MenuActivity.class), REQUEST_CODE_MENU);
                    } else {
                        int channelNum = data.getIntExtra(ExitActivity.RESULT_CHANNEL_NUM, -1);
                        presenter.switchChannel(channelNum);
                    }
                    return;
                case REQUEST_CODE_MENU:
                    parseMenuOperating(data);
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void parseMenuOperating(Intent data) {
        if (data != null) {
            int operatingCode = data.getIntExtra(RESULT_RESULT_OPERATING_CODE, -1);
            switch (operatingCode) {
                case OPERATING_CODE_EXIT:
                    onBackPressed();
                    break;
                case OPERATING_CODE_TOGGLE_RATIO:
                    mVideoView.toggleAspectRatio();
                    break;
                case OPERATING_CODE_TOGGLE_PLAYER:
                    mVideoView.togglePlayer();
                    break;
                case OPERATING_CODE_CHANNEL_MANAGER:
                    startActivity(ChannelManagerActivity.navigateToChannelManager(this, presenter.getCurrentChannelModel()));
                    break;
            }
        }
    }

    @Override
    public void togglePlayer() {
        mVideoView.togglePlayer();
    }

    @Override
    public void showVideoLoading() {
        if (!mProgressVideoLoading.isShown()) {
            mProgressVideoLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideVideoLoading() {
        if (mProgressVideoLoading.isShown()) {
            mProgressVideoLoading.setVisibility(View.GONE);
        }
    }

    //region Voice Support
    private class VoiceSceneListener implements ISceneListener {

        private final String SCENE_ID = getPackageName() + ":" + LivePlayActivity.class.getSimpleName();

        private final HashMap<String, String[]> VOICE_DICT = new HashMap<String, String[]>() {{
            put(getResources().getString(R.string.voice_type_last_channel), getResources().getStringArray(R.array.voice_dict_last_channel));
            put(getResources().getString(R.string.voice_type_next_channel), getResources().getStringArray(R.array.voice_dict_next_channel));
            put(getResources().getString(R.string.voice_type_next_source), getResources().getStringArray(R.array.voice_dict_next_source));
            put(getResources().getString(R.string.voice_type_back), getResources().getStringArray(R.array.voice_dict_back));
        }};

        @Override
        public String onQuery() {
            try {
                return JsonUtil.makeScenceJson(SCENE_ID, VOICE_DICT, null, null).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onExecute(Intent intent) {
            runOnUiThread(() -> {
                mVoiceFeedback.begin(intent);
                if (intent.hasExtra("_scene") && SCENE_ID.equals(intent.getStringExtra("_scene"))) {
                    if (intent.hasExtra("_command")) {
                        String command = intent.getStringExtra("_command");
                        presenter.doVoiceCommand(command);
                        mVoiceFeedback.feedback(command, Feedback.SILENCE);
                    }
                }
            });
        }
    }

    @Override
    public void tipsOpenVoiceSupport() {
        getSafetyHandler().postDelayed(() -> toast(getResources().getString(R.string.txt_open_voice_support)), 2 * 1000);
    }
    //endregion
}
