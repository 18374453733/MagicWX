package com.hdpfans.app.ui.main.presenter;

import com.hdpfans.app.frame.BaseView;
import com.hdpfans.app.frame.IPresenter;
import com.hdpfans.app.model.entity.Recommend;

/**
 * 退出页面V-P接口定义
 */
public interface ExitContract {

    interface View extends BaseView {
        /**
         * 显示App版本信息
         */
        void showVersionTips(String tips);

        /**
         * 显示运营推广
         */
        void showExitRecommend(Recommend exitRecommend);

        /**
         * 隐藏运营推广
         */
        void hideExitRecommend();

        /**
         * 跳转频道
         */
        void openChannel(int channelNum);

        /**
         * 显示插件信息
         */
        void showPluginInfo(String pluginInfo);

        /**
         * 显示软件推广下载进度
         */
        void showDownloadProgress(int totalSize, int downloadSize);

        /**
         * 隐藏软件推广下载进度
         */
        void hideDownloadProgress();
    }

    interface Presenter extends IPresenter<View> {

        /**
         * 点击推广按钮
         */
        void onClickGuide();
    }

}
