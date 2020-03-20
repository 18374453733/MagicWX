package com.hdpfans.plugin;

import android.content.Context;
import android.text.TextUtils;

import com.hdpfans.api.CntvApi;
import com.hdpfans.plugin.utils.CntvKooUtils;
import com.hdpfans.plugin.utils.Utils;

public class CntvPluginApi implements CntvApi {

    private Context mContext;

    @Override
    public void onCreate(Context context) {
        this.mContext = context;
    }

    @Override
    public String[] getKooDrmInfo(String url) {
        CntvKooUtils.getInstance().set(url);
        String mac = Utils.macAddress(mContext);
        if (CntvKooUtils.getInstance().R.containsKey("contentid")) {
            String contentId = CntvKooUtils.getInstance().R.get("contentid");
            if (!TextUtils.isEmpty(contentId) && !TextUtils.isEmpty(mac)) {
                return new String[]{
                        "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR=unitend,MIMETYPE=application/vnd.unitend.drm,IV=54923ff96a7bdb736f2a9fb94626b49b,CONTENTID="
                                + contentId + ",URI=https://drm.live.cntv.cn/udrm/udmCNTVGetLicense",
                        mac.replace(":", "").toLowerCase()};
            }
        }
        return new String[]{"", ""};
    }

    @Override
    public boolean isCntvKooUrl(String playUrl) {
        return !TextUtils.isEmpty(playUrl) &&
                (playUrl.contains("cctv.v.kcdnvip.com") || playUrl.contains("cctv.vb.5213.liveplay.myqcloud.com") || playUrl.contains("cctv.v.myalicdn.com"));
    }
}
