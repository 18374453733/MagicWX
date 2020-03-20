package com.hdpfans.api;

import com.hdpfans.api.annotation.Plugin;

@Plugin(main = "com.hdpfans.plugin.CntvPluginApi", pack = "hdp.jar")
public interface CntvApi extends Api {

    /**
     * 获取drm参数
     */
    String[] getKooDrmInfo(String url);

    /**
     * 是否是cntv m3u8协议
     */
    boolean isCntvKooUrl(String playUrl);

}
