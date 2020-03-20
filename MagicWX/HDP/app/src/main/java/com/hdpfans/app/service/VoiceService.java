package com.hdpfans.app.service;

import android.content.Intent;
import android.os.Handler;

import com.hdpfans.app.App;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.model.event.ToastEvent;
import com.hdpfans.app.ui.live.LivePlayActivity;
import com.hdpfans.app.ui.main.MainActivity;
import com.iflytek.xiri.AppService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hdp.player.ApiKeys;
import hdpfans.com.R;

public class VoiceService extends AppService {

    @Inject
    PrefManager mPrefManager;

    @Override
    protected void onInit() {
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        setTVLiveListener(new VoiceTVLiveListener());
        setAppListener(new VoiceAppListener());
    }

    class VoiceTVLiveListener implements ITVLiveListener {

        @Override
        public void onOpen(int i) {
            if (mPrefManager.isOpenVoiceSupport()) {
                Intent intent = buildVoiceIntent();
                intent.putExtra(ApiKeys.INTENT_API_CHANNEL_NUM, String.valueOf(i));
                startActivity(intent);
            } else {
                toastVoiceSupportOpen();
            }
        }

        @Override
        public void onChangeChannel(int i, int i1) {
            if (mPrefManager.isOpenVoiceSupport()) {
                Intent intent = buildVoiceIntent();
                intent.putExtra(ApiKeys.INTENT_API_CHANNEL_NUM, String.valueOf(i));
                startActivity(intent);
            } else {
                toastVoiceSupportOpen();
            }
        }

        @Override
        public void onChangeChannel(String s, String s1, int i) {
            if (mPrefManager.isOpenVoiceSupport()) {
                Intent intent = buildVoiceIntent();
                intent.putExtra(ApiKeys.INTENT_API_CHANNEL_NAME, s);
                startActivity(intent);
            } else {
                toastVoiceSupportOpen();
            }
        }

        @Override
        public void onNextChannel(int i) {
            if (mPrefManager.isOpenVoiceSupport()) {
                if (((App) getApplicationContext()).isRunningActivity(LivePlayActivity.class)) {
                    Intent intent = buildVoiceIntent();
                    intent.putExtra(ApiKeys.INTENT_API_NEXT_CHANNEL, true);
                    startActivity(intent);
                }
            } else {
                toastVoiceSupportOpen();
            }
        }

        @Override
        public void onPrevChannel(int i) {
            if (mPrefManager.isOpenVoiceSupport()) {
                if (((App) getApplicationContext()).isRunningActivity(LivePlayActivity.class)) {
                    Intent intent = buildVoiceIntent();
                    intent.putExtra(ApiKeys.INTENT_API_LAST_CHANNEL, true);
                    startActivity(intent);
                }
            } else {
                toastVoiceSupportOpen();
            }
        }
    }

    private void toastVoiceSupportOpen() {
        new Handler().postDelayed(() -> EventBus.getDefault().post(new ToastEvent(getText(R.string.txt_open_voice_support))), 2 * 1000);
    }

    private Intent buildVoiceIntent() {
        Class activity = ((App) getApplicationContext()).isRunningActivity(LivePlayActivity.class) ? LivePlayActivity.class : MainActivity.class;
        Intent intent = new Intent(getApplicationContext(), activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    class VoiceAppListener implements IAppListener {

        @Override
        public void onTextFilter(Intent intent) {

        }

        @Override
        public void onExecute(Intent intent) {

        }
    }
}
