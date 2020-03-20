package com.hdpfans.app.ui.live.presenter;

import javax.inject.Inject;

import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;

@ActivityScope
public class MenuPresenter extends BasePresenter<MenuContract.View> implements MenuContract.Presenter {

    @Inject
    public MenuPresenter() {

    }

}
