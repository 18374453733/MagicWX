package com.hdpfans.api;


import android.util.Pair;

import com.hdpfans.api.annotation.Plugin;

import java.util.List;
import java.util.Map;

/**
 * 插件接口
 */
@Plugin(main = "com.hdpfans.plugin.HdpPluginApi", pack = "hdp.jar")
public interface HdpApi extends Api {

    /**
     * 获取节目源原始播放地址
     */
    Pair<String, Map<String, String>> getOriginalUrl(int num, String url);

    /**
     * 部分p2p节目源需要停止解析
     */
    void stopResolveUrl();

    /**
     * 是否放行屏蔽
     */
    boolean inspectBlock(int channelNum);

    @Deprecated
    boolean inspectBlock();

    /**
     * 获取区域名称
     */
    String getRegion();

    /**
     * 获取当前的EPG信息
     */
    String getCurrentEpg(String epgId);

    /**
     * 通过epg判断当前节目是否处于屏蔽阶段
     */
    boolean getCurrentEpgIsBlocked(String epgId);

    /**
     * 获取当前和下一个节目的EPG信息
     */
    Pair<String, String> getCurrentEpgWithNext(String epgId);

    /**
     * 动态更改调整节目源信息及位序
     */
    @Deprecated
    List<String> parseChannelSourceList(List<String> urls);

    List<String> parseChannelSourceList(int channelNum, List<String> urls);
}
