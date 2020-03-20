package com.hdpfans.app.ui.live.presenter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelModel;

import javax.inject.Inject;

import static com.hdpfans.app.ui.live.ChannelSourceListActivity.INTENT_PARAMS_CHANNEL;
import static com.hdpfans.app.ui.live.ChannelSourceListActivity.INTENT_PARAMS_INDEX;

@ActivityScope
public class ChannelSourceListPresenter extends BasePresenter<ChannelSourceListContract.View>
        implements ChannelSourceListContract.Presenter {

    private ChannelModel mCurrentModel;

    @Inject
    public ChannelSourceListPresenter() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void getChannelInfo() {
        mCurrentModel = getIntent().getParcelableExtra(INTENT_PARAMS_CHANNEL);
        int currentSourceIndex = getIntent().getIntExtra(INTENT_PARAMS_INDEX, 0);

        if (mCurrentModel != null) {
            getView().showChannelSourceList(mCurrentModel.getUrls(), currentSourceIndex > mCurrentModel.getUrls().size() ? 0 : currentSourceIndex);
        }
    }

}
