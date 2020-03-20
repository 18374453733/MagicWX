package com.hdpfans.plugin.source;

import android.content.Context;
import android.util.Pair;

import com.hdpfans.plugin.compat.RegionCompat;
import com.hdpfans.plugin.model.RegionModel;

import java.util.List;

public class SourcePutter {

    private static SourcePutter sInstance;

    private SourcePutter() {
    }

    public static SourcePutter get() {
        if (sInstance == null) {
            synchronized (SourcePutter.class) {
                if (sInstance == null) {
                    sInstance = new SourcePutter();
                }
            }
        }
        return sInstance;
    }

    public List<String> create(Context context, int channelNum, List<String> originalUrls) {
        if (originalUrls == null) return null;

        if (isTelecom(context)) {
            AiShangSourceServing aiShangSourceServing = new AiShangSourceServing();
            Pair<Integer, String> urlWithIndex = aiShangSourceServing.create(channelNum);
            if (urlWithIndex != null) {
                originalUrls.add(urlWithIndex.first, urlWithIndex.second);
            }
        }
        return originalUrls;
    }

    private boolean isTelecom(Context context) {
        RegionModel regionModel = RegionCompat.getsInstance(context).getRegionModel();
        if (regionModel != null) {
            return regionModel.getIsp().contains("电信");
        }
        return false;
    }

    private boolean isCity(Context context, String cityName) {
        RegionModel regionModel = RegionCompat.getsInstance(context).getRegionModel();
        if (regionModel != null) {
            return regionModel.getCity().contains(cityName);
        }
        return false;
    }
}
