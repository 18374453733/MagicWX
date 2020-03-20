package com.hdpfans.app.ui.main.presenter;

import com.hdpfans.app.frame.BaseView;
import com.hdpfans.app.frame.IPresenter;

public interface MainContract {

    interface View extends BaseView {
        void showNeedUpdateDialog(String title, String message, boolean force);

        void navigateToLivePlay();

        void showLaunchImage(String launchImage);

        void showDownloadingProgress(int totalSize, int downloadSize, String percent);

        void hideDownloadProgress();

        void showNetworkErrorDialog();

        void hideLaunchBackground();
    }

    interface Presenter extends IPresenter<View> {
        void downloadNewVersionApk();

        void getUpdateConfig();
    }

}
