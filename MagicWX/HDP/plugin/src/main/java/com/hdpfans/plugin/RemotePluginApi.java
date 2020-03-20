package com.hdpfans.plugin;

import android.content.Context;

import com.hdpfans.api.RemoteApi;
import com.hdpfans.plugin.utils.Utils;

public class RemotePluginApi implements RemoteApi {

    private Context mContext;

    @Override
    public boolean isOpenUploadSource() {
        return !"dangbei".equals(Utils.buildFlavor(mContext));
    }

    @Override
    public int getRemotePort() {
        return 12321;
    }

    @Override
    public void onCreate(Context context) {
        this.mContext = context;
    }
}
