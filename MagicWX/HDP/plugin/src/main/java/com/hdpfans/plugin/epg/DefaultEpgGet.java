package com.hdpfans.plugin.epg;

import android.content.Context;
import android.util.Pair;

import com.hdpfans.plugin.model.EpgInfoModel;

public class DefaultEpgGet extends EpgGet {

    public DefaultEpgGet(Context context, String epgId) {
        super(context, epgId);
    }

    @Override
    public String getCurrentEpg() {
        return NO_EPG;
    }

    @Override
    public Pair<String, String> getCurrentEpgWithNext() {
        return new Pair<>(NO_EPG, NO_EPG);
    }

    @Override
    public EpgInfoModel getCurrentEpgInfo() {
        EpgInfoModel epgInfoModel = new EpgInfoModel();
        epgInfoModel.setTitle(NO_EPG);
        return epgInfoModel;
    }
}
