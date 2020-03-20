package com.hdpfans.app.ui.live.presenter;

import java.util.List;

import com.hdpfans.app.frame.BaseView;
import com.hdpfans.app.frame.IPresenter;

public interface ChannelSourceListContract {

    interface View extends BaseView {

        void showChannelSourceList(List<String> urls, int currentSourceIndex);
    }

    interface Presenter extends IPresenter<View> {

    }

}
