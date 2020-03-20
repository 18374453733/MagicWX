package com.hdpfans.plugin.epg;

import android.content.Context;
import android.text.TextUtils;

public final class EpgFactory {

    private static EpgFactory sInstance;

    public static EpgFactory get() {
        if (sInstance == null) {
            synchronized (EpgGet.class) {
                if (sInstance == null) {
                    sInstance = new EpgFactory();
                }
            }
        }
        return sInstance;
    }

    public EpgGet create(Context context, String epgId) {
        if (TextUtils.isEmpty(epgId)) {
            return new DefaultEpgGet(context, epgId);
        }

        if (epgId.startsWith("tvsou-")){
            return new TvSouEpgGet(context, epgId);
        } else {
            return new DefaultEpgGet(context, epgId);
        }
    }


}
