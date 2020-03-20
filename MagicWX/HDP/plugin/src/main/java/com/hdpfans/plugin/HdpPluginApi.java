package com.hdpfans.plugin;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.hdpfans.api.HdpApi;
import com.hdpfans.plugin.compat.RegionCompat;
import com.hdpfans.plugin.compat.WhitelistCompat;
import com.hdpfans.plugin.epg.EpgFactory;
import com.hdpfans.plugin.model.RegionModel;
import com.hdpfans.plugin.source.SourcePutter;
import com.hdpfans.plugin.spider.DefaultProxySpider;
import com.hdpfans.plugin.spider.SpiderBoss;
import com.hdpfans.plugin.spider.StraySpider;
import com.hdpfans.plugin.spider.cntv.CntvLocalSpider;
import com.hdpfans.plugin.spider.cntv.CntvSpider;
import com.hdpfans.plugin.spider.dsj.DsjSpider;
import com.hdpfans.plugin.spider.dsj.TvBusSpider;
import com.hdpfans.plugin.spider.http.Http105;
import com.hdpfans.plugin.spider.http.Http112;
import com.hdpfans.plugin.spider.http.Http125;
import com.hdpfans.plugin.spider.http.Http153;
import com.hdpfans.plugin.spider.http.Http154;
import com.hdpfans.plugin.spider.http.Http156;
import com.hdpfans.plugin.spider.http.Http163;
import com.hdpfans.plugin.spider.http.Http167;
import com.hdpfans.plugin.spider.http.Http216;
import com.hdpfans.plugin.spider.http.Http51;
import com.hdpfans.plugin.spider.http.Http801;
import com.hdpfans.plugin.spider.proxy.AiShangDliSpider;
import com.hdpfans.plugin.spider.proxy.Dli16Spider;
import com.hdpfans.plugin.spider.proxy.LiveDliSpider;
import com.hdpfans.plugin.spider.proxy.migu.MigiSpider;
import com.hdpfans.plugin.spider.proxy.migu.Migu2Spider;
import com.hdpfans.plugin.spider.proxy.migu.MiguTVSpider;
import com.hdpfans.plugin.spider.proxy.migu.MiguXIIISpider;
import com.hdpfans.plugin.spider.shop.ShopSpider;
import com.hdpfans.plugin.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HdpPluginApi implements HdpApi {

    private Context mContext;

    private List<SpiderBoss> mSpiders = new ArrayList<>();

    @Override
    public void onCreate(Context context) {
        this.mContext = context;

        RegionCompat.getsInstance(mContext);

        mSpiders.addAll(Arrays.asList(
                new ShopSpider(mContext),

                new Migu2Spider(mContext),
                new MiguXIIISpider(mContext),
                new MigiSpider(mContext),
                new MiguTVSpider(mContext),

                new AiShangDliSpider(mContext),
                new Dli16Spider(mContext),

                new LiveDliSpider(mContext),
                new DsjSpider(mContext),
                new TvBusSpider(mContext),
                new CntvLocalSpider(mContext),
                new CntvSpider(mContext),

                new DefaultProxySpider(mContext),   // 默认代理蜘蛛

                new Http51(mContext),
                new Http105(mContext),
                new Http112(mContext),
                new Http125(mContext),
                new Http153(mContext),
                new Http154(mContext),
                new Http156(mContext),
                new Http163(mContext),
                new Http167(mContext),
                new Http216(mContext),
                new Http801(mContext),

                new StraySpider(mContext)           // 鬼知道的代理蜘蛛
        ));
    }

    @Override
    public Pair<String, Map<String, String>> getOriginalUrl(int num, String url) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        for (SpiderBoss spider : mSpiders) {
            if (spider.hint(url)) {
                return spider.silking(url);
            }
        }
        return new Pair<>(url, null);
    }

    @Override
    public void stopResolveUrl() {
        for (SpiderBoss spider : mSpiders) {
            spider.excretion();
        }
    }

    @Override
    public boolean inspectBlock() {
        return true;
    }

    @Override
    public boolean inspectBlock(int channelNum) {
        if ("dangbei".equals(Utils.buildFlavor(mContext)) && channelNum == 105) {
            return true;
        }
        return !WhitelistCompat.getInstance().isInWhitelist(mContext);
    }

    @Override
    public String getRegion() {
        RegionModel regionModel = RegionCompat.getsInstance(mContext).getRegionModel();
        if (regionModel != null) {
            return regionModel.getRegion();
        }
        return null;
    }

    @Override
    public String getCurrentEpg(String epgId) {
        return EpgFactory.get().create(mContext, epgId).getCurrentEpg();
    }

    @Override
    public boolean getCurrentEpgIsBlocked(String epgId) {
        return EpgFactory.get().create(mContext, epgId).getCurrentEpgInfo().isBlock();
    }

    @Override
    public Pair<String, String> getCurrentEpgWithNext(String epgId) {
        return EpgFactory.get().create(mContext, epgId).getCurrentEpgWithNext();
    }

    @Override
    public List<String> parseChannelSourceList(List<String> urls) {
        return urls;
    }

    @Override
    public List<String> parseChannelSourceList(int channelNum, List<String> urls) {
        return SourcePutter.get().create(mContext, channelNum, urls);
    }
}
