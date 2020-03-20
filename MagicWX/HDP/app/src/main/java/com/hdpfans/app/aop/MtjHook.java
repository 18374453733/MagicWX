package com.hdpfans.app.aop;

import android.content.Context;

import com.baidu.mobstat.StatService;
import com.hdpfans.app.App;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.data.manager.Settings;
import com.hdpfans.app.model.annotation.BootChannelMode;
import com.hdpfans.app.model.annotation.DisplayTextSizeMode;
import com.hdpfans.app.model.annotation.MediaCodecType;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.live.presenter.LivePlayPresenter;
import com.hdpfans.app.ui.widget.media.IRenderView;
import com.hdpfans.app.ui.widget.media.IjkVideoView;
import com.hdpfans.app.utils.BoxCompat;
import com.hdpfans.app.utils.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class MtjHook {

    private static final String TAG = MtjHook.class.getSimpleName();

    private Context context;
    private LivePlayPresenter livePlayPresenter;
    private Settings mSettings;
    private PrefManager mPrefManager;

    /**
     * 统计用户类型
     */
    @After("execution(* com.hdpfans.app.App.onCreate())")
    public void getAppContext(JoinPoint joinPoint) {
        this.context = ((App) joinPoint.getThis());
        this.mSettings = new Settings(context);
        this.mPrefManager = new PrefManager(context);

        String userType = BoxCompat.isPhoneRunning(context) ? "手机用户" : "盒子用户";
        StatService.onEvent(context, Events.EVENT_LUNCH_USER_MODE, userType);
        Logger.LOGI(TAG, Events.EVENT_LUNCH_USER_MODE + ": " + userType);
    }

    /**
     * 统计开机频道
     */
    @After("execution(* com.hdpfans.app.ui.live.LivePlayActivity.onCreate(..))")
    public void onStartChannelType() {
        PrefManager prefManager = new PrefManager(context);
        String bootChannelType;
        switch (prefManager.getBootChannelMode()) {
            case BootChannelMode.COLLECT:
                bootChannelType = "收藏优先";
                break;
            case BootChannelMode.LAST_WATCH:
                bootChannelType = "上次观看";
                break;
            case BootChannelMode.DEFAULT:
            default:
                bootChannelType = "系统默认";
                break;
        }
        StatService.onEvent(context, Events.EVENT_LUNCH_CHANNEL_TYPE, bootChannelType);
        Logger.LOGI(TAG, Events.EVENT_LUNCH_CHANNEL_TYPE + ": " + bootChannelType);
    }

    @After("execution(com.hdpfans.app.ui.live.presenter.LivePlayPresenter.new(..))")
    public void onLivePlayPresenter(JoinPoint joinPoint) {
        this.livePlayPresenter = ((LivePlayPresenter) joinPoint.getThis());
    }

    /**
     * 统计当前播放的频道
     */
    @After("execution(* com.hdpfans.app.ui.widget.media.IjkVideoView.start(..))")
    public void onSwitchChannel(JoinPoint joinPoint) {
        if (((IjkVideoView) joinPoint.getThis()).isInPlaybackState() && this.livePlayPresenter != null) {
            StatService.onEvent(context, Events.EVENT_CHANNEL_SWITCH, livePlayPresenter.getCurrentChannelModel().getName());
            Logger.LOGI(TAG, Events.EVENT_CHANNEL_SWITCH + ": " + livePlayPresenter.getCurrentChannelModel().getName());
        }
    }

    /**
     * 统计点击退出推荐
     */
    @After("call(* com.hdpfans.app.ui.main.presenter.ExitPresenter.onClickGuide(..))")
    public void onClickExitRecommend() {
        StatService.onEvent(context, Events.EVENT_EXIT_CLICK_RECOMMEND, "pass", 1);
        Logger.LOGI(TAG, Events.EVENT_EXIT_CLICK_RECOMMEND + ": pass");
    }

    /**
     * 统计下载退出推荐
     */
    @After("execution(* com.hdpfans.app.ui.main.presenter.ExitPresenter.downloadApkFile(..))")
    public void onClickExitRecommendDownloadApp() {
        StatService.onEvent(context, Events.EVENT_CLICK_RECOMMEND_APP, "download");
        Logger.LOGI(TAG, Events.EVENT_CLICK_RECOMMEND_APP + ": download");
    }

    /**
     * 统计下载屏蔽推荐
     */
    @After("call(* com.hdpfans.app.ui.live.presenter.ChannelListPresenter.downloadApkFile(..))")
    public void onClickBlockRecommendDownloadApp() {
        StatService.onEvent(context, Events.EVENT_CLICK_RECOMMEND_APP, "download");
        Logger.LOGI(TAG, Events.EVENT_CLICK_RECOMMEND_APP + ": download");
    }

    @Before("call (* com.hdpfans.app.ui.live.fragment.SettingFragment.onSettingChanged(int, boolean))")
    public void onClickMenu(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        try {
            int settingCode = ((int) args[0]);
            String menuEvent = "未知";
            switch (settingCode) {
                case MenuActivity.OPERATING_CODE_EXIT:
                    menuEvent = "退出";
                    break;
                case MenuActivity.OPERATING_CODE_TOGGLE_RATIO:
                    menuEvent = getToggleRatioEvent();
                    break;
                case MenuActivity.OPERATING_CODE_TOGGLE_PLAYER:
                    menuEvent = getTogglePlayerEvent();
                    break;
                case MenuActivity.OPERATING_CODE_CHANNEL_MANAGER:
                    menuEvent = "点击频道管理";
                    break;
                case MenuActivity.OPERATING_CODE_HELP:
                    menuEvent = "疑问帮助";
                    break;
                case MenuActivity.OPERATING_CODE_FEATURE:
                    menuEvent = "新版本特征";
                    break;
                case MenuActivity.OPERATING_CODE_REGION_CHANNEL:
                    menuEvent = getRegionChannelEvent();
                    break;
                case MenuActivity.OPERATING_CODE_BOOT_CHANNEL:
                    menuEvent = getBootChannelEvent();
                    break;
                case MenuActivity.OPERATING_CODE_TEXT_SIZE:
                    menuEvent = getTextSizeEvent();
                    break;
                case MenuActivity.OPERATING_CODE_EPG:
                    menuEvent = getOpenEpgEvent();
                    break;
                case MenuActivity.OPERATING_CODE_VOICE_SUPPORT:
                    menuEvent = getVoiceSupportEvent();
                    break;
                case MenuActivity.OPERATING_CODE_SYSTEM_BOOT:
                    menuEvent = getSystemBootEvent();
                    break;
                case MenuActivity.OPERATING_CODE_CLEAR_CACHE:
                    menuEvent = "清除缓存";
                    break;
                case MenuActivity.OPERATING_CODE_REGION:
                    menuEvent = "设置省份";
                    break;
            }

            StatService.onEvent(context, Events.EVENT_CLICK_MENU, menuEvent);
            Logger.LOGI(TAG, Events.EVENT_CLICK_MENU + ": " + menuEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getSystemBootEvent() {
        if (mPrefManager.isOpenSystemBoot()) {
            return "开启开机启动";
        } else {
            return "关闭开机启动";
        }
    }

    private String getVoiceSupportEvent() {
        if (mPrefManager.isOpenVoiceSupport()) {
            return "开启语音支持";
        } else {
            return "关闭语音支持";
        }
    }

    private String getOpenEpgEvent() {
        if (mPrefManager.isOpenChannelEpg()) {
            return "开启EPG显示";
        } else {
            return "关闭EPG显示";
        }
    }

    private String getTextSizeEvent() {
        switch (mPrefManager.getDisplayTextSizeMode()) {
            case DisplayTextSizeMode.MIDDLE:
                return "设置中字体";
            case DisplayTextSizeMode.BIG:
                return "设置大字体";
            case DisplayTextSizeMode.SMALL:
                return "设置小字体";
            default:
                return "未知";
        }
    }

    private String getToggleRatioEvent() {
        switch (mSettings.getAspectRatio()) {
            case IRenderView.AR_ASPECT_FIT_PARENT:
                return "画面比例全屏";
            case IRenderView.AR_ASPECT_FILL_PARENT:
                return "画面比例原始";
            case IRenderView.AR_16_9_FIT_PARENT:
                return "画面比例16:9";
            case IRenderView.AR_4_3_FIT_PARENT:
                return "画面比例4:3";
            default:
                return "未知";
        }
    }

    private String getTogglePlayerEvent() {
        switch (mSettings.getUsingMediaCodec()) {
            case MediaCodecType.SMART:
                return "切换智能解码";
            case MediaCodecType.HARD:
                return "切换硬解解码";
            case MediaCodecType.SOFT:
                return "切换软解解码";
            default:
                return "未知";
        }
    }

    private String getRegionChannelEvent() {
        if (mPrefManager.getRegionChannelVisibility()) {
            return "显示外省节目";
        } else {
            return "隐藏外省节目";
        }
    }

    private String getBootChannelEvent() {
        switch (mPrefManager.getBootChannelMode()) {
            case BootChannelMode.DEFAULT:
                return "开机频道系统默认";
            case BootChannelMode.COLLECT:
                return "开机频道收藏优先";
            case BootChannelMode.LAST_WATCH:
                return "开机频道上次观看";
            default:
                return "未知";
        }
    }

    /**
     * 百度统计事件关键字
     */
    static class Events {
        /**
         * 启动用户类型
         */
        static final String EVENT_LUNCH_USER_MODE = "event_lunch_user_mode";

        /**
         * 开机频道类型
         */
        static final String EVENT_LUNCH_CHANNEL_TYPE = "event_lunch_channel_type";

        /**
         * 频道切换
         */
        static final String EVENT_CHANNEL_SWITCH = "event_channel_switch";

        /**
         * 退出页面点击推荐
         */
        static final String EVENT_EXIT_CLICK_RECOMMEND = "click_download_exitpage";

        /**
         * 下载推荐应用
         */
        static final String EVENT_CLICK_RECOMMEND_APP = "download_recommend_app";

        /**
         * 点击菜单栏
         */
        static final String EVENT_CLICK_MENU = "click_menu";
    }
}
