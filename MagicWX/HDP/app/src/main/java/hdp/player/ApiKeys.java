package hdp.player;

import android.content.Intent;

public final class ApiKeys {
    /**
     * API参数：频道编号
     */
    public static final String INTENT_API_CHANNEL_NUM = "ChannelNum";
    /**
     * API参数：播放地址
     */
    public static final String INTENT_API_CHANNEL_URL = "ChannelUrl";
    /**
     * API参数：频道名称
     */
    public static final String INTENT_API_CHANNEL_NAME = "ChannelName";
    /**
     * API参数：隐藏启动图片
     */
    public static final String INTENT_API_HIDE_LOADING_IMAGE = "HIDE_LOADING_DEFAULT";
    /**
     * API参数：隐藏退出界面
     */
    public static final String INTENT_API_HIDE_EXIT = "HIDE_EXIT_DIAG";
    /**
     * API参数：下一个频道
     */
    public static final String INTENT_API_NEXT_CHANNEL = "INTENT_API_NEXT_CHANNEL";
    /**
     * API参数：上一个频道
     */
    public static final String INTENT_API_LAST_CHANNEL = "INTENT_API_LAST_CHANNEL";

    public static void copyApiParams(Intent originalIntent, Intent newIntent) {
        if (originalIntent == null || newIntent == null) return;

        if (originalIntent.hasExtra(INTENT_API_CHANNEL_NUM)) {
            newIntent.putExtra(INTENT_API_CHANNEL_NUM, originalIntent.getIntExtra(INTENT_API_CHANNEL_NUM, 1));
        }
        if (originalIntent.hasExtra(INTENT_API_CHANNEL_URL)) {
            newIntent.putExtra(INTENT_API_CHANNEL_URL, originalIntent.getStringExtra(INTENT_API_CHANNEL_URL));
        }
        if (originalIntent.hasExtra(INTENT_API_CHANNEL_NAME)) {
            newIntent.putExtra(INTENT_API_CHANNEL_NAME, originalIntent.getStringExtra(INTENT_API_CHANNEL_NAME));
        }
        if (originalIntent.hasExtra(INTENT_API_HIDE_LOADING_IMAGE)) {
            newIntent.putExtra(INTENT_API_HIDE_LOADING_IMAGE, originalIntent.getBooleanExtra(INTENT_API_HIDE_LOADING_IMAGE, false));
        }
        if (originalIntent.hasExtra(INTENT_API_HIDE_EXIT)) {
            newIntent.putExtra(INTENT_API_HIDE_EXIT, originalIntent.getBooleanExtra(INTENT_API_HIDE_EXIT, false));
        }
        if (originalIntent.hasExtra(INTENT_API_NEXT_CHANNEL)) {
            newIntent.putExtra(INTENT_API_NEXT_CHANNEL, originalIntent.getBooleanExtra(INTENT_API_NEXT_CHANNEL, false));
        }
        if (originalIntent.hasExtra(INTENT_API_LAST_CHANNEL)) {
            newIntent.putExtra(INTENT_API_LAST_CHANNEL, originalIntent.getBooleanExtra(INTENT_API_LAST_CHANNEL, false));
        }
    }
}
