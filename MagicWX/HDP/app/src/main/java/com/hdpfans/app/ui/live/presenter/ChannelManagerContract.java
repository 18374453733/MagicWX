package com.hdpfans.app.ui.live.presenter;

import java.util.List;

import com.hdpfans.app.frame.BaseView;
import com.hdpfans.app.frame.IPresenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;

public interface ChannelManagerContract {

    interface View extends BaseView {
        void showChannelType(List<ChannelTypeModel> channelTypeModels);

        void showChannelList(List<ChannelModel> channelModels);

        void showCollectTips(int totalSize, int collectSize);

        void showCollectList(List<ChannelModel> channelModels);

        void addCollectedChannel(ChannelModel channelModel);

        void removeCollectedChannel(ChannelModel channelModel);
    }

    interface Presenter extends IPresenter<View> {

        void onSelectedChannelType(ChannelTypeModel channelTypeModel);

        void hiddenChannelOrNot(ChannelModel channelModel);

        void collectChannelOrNot(ChannelModel channelModel);

        void hiddenChannelTypeOrNot(ChannelTypeModel channelTypeModel);
    }

}
