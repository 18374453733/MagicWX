package com.hdpfans.api;

import com.hdpfans.api.annotation.Plugin;

@Plugin(main = "com.hdpfans.plugin.RemotePluginApi", pack = "hdp.jar")
public interface RemoteApi extends Api{
    boolean isOpenUploadSource();

    /**
     * 远程端口
     */
    int getRemotePort();

}
