package com.hdpfans.app.ui.live.presenter;

import com.hdpfans.app.frame.BaseView;
import com.hdpfans.app.frame.IPresenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.Recommend;

import java.util.List;

public interface ChannelListContract {

    interface View extends BaseView {

        void showChannelType(String name);

        void showChannelList(List<ChannelModel> channelList, int defaultNum);

        void resultChannel(ChannelModel channel);

        void showCopyrightRecommend(Recommend blockRecommend, String btnText);

        void refreshTime();

        void setTips(String s);

        void navigateToApkByPackage(String packageName);

        void showDownloadProgress(int totalSize, int downloadSize, String percent);

        void hideDownloadProgress();
    }

    interface Presenter extends IPresenter<View> {

        void loadBeforeChannelList();

        void loadBeforeChannelListLastChannel();

        void loadAlterChannelList();

        void loadAlterChannelListFirstChannel();

        void checkedChannel(ChannelModel channelModel);

        void onLongClockChannel(ChannelModel channelModel);

        void openOrDownloadRecApk();
    }

}
