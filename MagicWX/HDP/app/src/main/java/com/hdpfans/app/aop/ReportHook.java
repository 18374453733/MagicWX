package com.hdpfans.app.aop;

import com.google.gson.Gson;
import com.hdpfans.api.StatisticsApi;
import com.hdpfans.app.App;
import com.hdpfans.app.ui.live.presenter.LivePlayPresenter;
import com.hdpfans.app.ui.widget.media.IjkVideoView;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONException;
import org.json.JSONObject;

@Aspect
public class ReportHook {

    private volatile long startPlayerTime = 0;

    private App app;
    private LivePlayPresenter livePlayPresenter;

    @After("execution(* com.hdpfans.app.App.onCreate())")
    public void getAppContext(JoinPoint joinPoint) {
        this.app = ((App) joinPoint.getThis());
    }

    @After("execution(com.hdpfans.app.ui.live.presenter.LivePlayPresenter.new(..))")
    public void onLivePlayPresenter(JoinPoint joinPoint) {
        this.livePlayPresenter = ((LivePlayPresenter) joinPoint.getThis());
    }

    @After("execution(* com.hdpfans.app.ui.widget.media.IjkVideoView.start(..))")
    public void onPlayerStart(JoinPoint joinPoint) {
        if (((IjkVideoView) joinPoint.getThis()).isInPlaybackState()) {
            startPlayerTime = System.currentTimeMillis();
            StatisticsApi api = app.getPluginLoader().createApi(StatisticsApi.class);
            if (api != null && livePlayPresenter.getCurrentChannelModel() != null) {
                try {
                    api.onPlayChannel(new JSONObject(new Gson().toJson(livePlayPresenter.getCurrentChannelModel())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                startPlayerTime = 0;
            }
        }
    }

    @After("execution(* com.hdpfans.app.ui.widget.media.IjkVideoView.release(*))")
    public void onPlayerStop() {
        if (startPlayerTime != 0) {
            StatisticsApi api = app.getPluginLoader().createApi(StatisticsApi.class);
            if (api != null) {
                try {
                    api.onStopChannel(
                            new JSONObject(new Gson().toJson(livePlayPresenter.getCurrentChannelModel())),
                            (System.currentTimeMillis() - startPlayerTime) / 1000
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            startPlayerTime = 0;
        }
    }

    @Before("call(* com.hdpfans.app.utils.PhoneCompat.exit(..))")
    public void onExitApp() {
        StatisticsApi api = app.getPluginLoader().createApi(StatisticsApi.class);
        if (api != null) {
            api.onExitApp();
        }
    }

}
