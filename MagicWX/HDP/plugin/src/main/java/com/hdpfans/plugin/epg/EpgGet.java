package com.hdpfans.plugin.epg;

import android.content.Context;
import android.util.Pair;

import com.hdpfans.plugin.model.EpgInfoModel;

public abstract class EpgGet {

    protected static final String NO_EPG = "暂无节目信息";

    private Context mContext;
    private String mEpgId;

    public EpgGet(Context context, String epgId) {
        this.mContext = context;
        this.mEpgId = epgId;
    }

    public Context getContext() {
        return mContext;
    }

    public String getEpgId() {
        return mEpgId;
    }

    public abstract String getCurrentEpg();

    public abstract Pair<String, String> getCurrentEpgWithNext();

    public abstract EpgInfoModel getCurrentEpgInfo();
}
