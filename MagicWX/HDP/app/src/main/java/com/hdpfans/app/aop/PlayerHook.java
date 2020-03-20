package com.hdpfans.app.aop;

import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.ui.live.presenter.LivePlayPresenter;
import com.hdpfans.app.utils.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Locale;

@Aspect
public class PlayerHook {

    private static final String TAG = PlayerHook.class.getSimpleName();

    @Before("execution(* com.hdpfans.app.ui.live.presenter.LivePlayPresenter.switchChannelNextSource())")
    public void onBeforeSwitchChannelNextSource(JoinPoint joinPoint) {
        LivePlayPresenter livePlayPresenter = (LivePlayPresenter) joinPoint.getThis();
        ChannelModel currentChannelModel = livePlayPresenter.getCurrentChannelModel();
        Integer index = livePlayPresenter.getPlayerRecordingIndex().get(currentChannelModel.getNum());
        if (index == null) {
            Logger.LOGD(TAG, String.format(Locale.getDefault(), "%s Source Play Error: index -> Unknown, Source -> Unknown",
                    currentChannelModel.getName()));
        } else {
            Logger.LOGD(TAG, String.format(Locale.getDefault(), "%s Source Play Error: index -> %d, Source -> %s",
                    currentChannelModel.getName(),
                    index,
                    currentChannelModel.getUrls().get(index)));
        }
    }

    @After("execution(* com.hdpfans.app.ui.live.presenter.LivePlayPresenter.switchChannelNextSource())")
    public void onAfterSwitchChannelNextSource(JoinPoint joinPoint) {
        LivePlayPresenter livePlayPresenter = (LivePlayPresenter) joinPoint.getThis();
        ChannelModel currentChannelModel = livePlayPresenter.getCurrentChannelModel();
        Integer index = livePlayPresenter.getPlayerRecordingIndex().get(currentChannelModel.getNum());
        if (index == null) {
            Logger.LOGD(TAG, String.format(Locale.getDefault(), "%s Switch Next Source: unknown!", currentChannelModel.getName()));
        } else if (index < currentChannelModel.getUrls().size()) {
            Logger.LOGD(TAG, String.format(Locale.getDefault(), "%s Switch Next Source: index -> %d, Source: %s", currentChannelModel.getName(), index, currentChannelModel.getUrls().get(index)));
        }
    }
}
