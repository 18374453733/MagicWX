package com.hdpfans.app.ui.live.presenter;

import android.content.Intent;
import android.util.Pair;

import com.hdpfans.app.frame.BaseView;
import com.hdpfans.app.frame.IPresenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.utils.CopyrightBlockChecker;
import com.hdpfans.app.utils.Optional;

import java.util.List;
import java.util.Map;

public interface LivePlayContract {

    interface View extends BaseView {
        void playVideo(String url, Map<String, String> header, CopyrightBlockChecker copyrightBlockChecker);

        void playKooVideo(String url, String[] drmInfo, CopyrightBlockChecker copyrightBlockChecker);

        void navigateToChannelList(ChannelModel channelModel);

        void showShopReproduction(String url);

        void showMediaPlayFail();

        void showNoCopyright();

        void navigateToChannelSourceList(ChannelModel currentChannelModel, int index);

        void showCurrentChannelInfo(ChannelModel channelModel, Optional<Pair<String, String>> epg);

        void hideCurrentChannelInfo();

        void showOnKeyChannels(String keyNum, List<ChannelModel> channelModels);

        void hideOnKeyChannel();

        void backPress();

        void tipsOpenVoiceSupport();

        void togglePlayer();

        void showVideoLoading();

        void hideVideoLoading();
    }

    interface Presenter extends IPresenter<View> {
        void onParseIntentToPlay(Intent intent);

        void onClickShowChannelList();

        void switchChannel(ChannelModel channelModel);

        void switchChannel(int channelNum);

        void switchChannelNextSource();

        void switchChannelNextSource(boolean record);

        void switchAfterChannel();

        void switchBeforeChannel();

        void showChannelSourceList();

        void switchChannelSource(int index);

        void reloadChannel();

        void onKeyChannelByNumber(int i);

        boolean isFromApiCall();

        void doVoiceCommand(String command);
    }

}
