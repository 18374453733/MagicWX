package com.hdpfans.app.utils;

import android.text.TextUtils;

import com.hdpfans.api.HdpApi;
import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.model.entity.BlockInfoModel;
import com.hdpfans.app.model.entity.BlockTimesModel;
import com.hdpfans.app.model.entity.BlockVersionChannel;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.utils.plugin.PluginLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CopyrightBlockChecker {

    @Inject
    HdpRepository mHdpRepository;
    @Inject
    PluginLoader mPluginLoader;

    private int channelNum;

    @Inject
    public CopyrightBlockChecker() {
    }

    public boolean isInBlockTime(int channelNum) {
        Map<Integer, BlockInfoModel> blocksInfo = mHdpRepository.getBlocksInfo();
        if (blocksInfo != null) {
            BlockInfoModel blockInfoModel = blocksInfo.get(channelNum);
            // 判断是否屏蔽当前频道
            if (blockInfoModel != null) {
                boolean isBlocked = isTimeMatched(blockInfoModel) && isRegionMatched(blockInfoModel);

                // 判断是否放开屏蔽
                HdpApi hdpApi = mPluginLoader.createApi(HdpApi.class);
                if (isBlocked && hdpApi != null) {
                    return hdpApi.inspectBlock(channelNum);
                }

                return isBlocked;
            }
        }
        return false;
    }

    public List<String> getBlockChannelSource(ChannelModel channelModel) {
        HdpApi hdpApi = mPluginLoader.createApi(HdpApi.class);
        if (hdpApi == null) {
            return channelModel.getUrls();
        }
        List<String> blockUrls = new ArrayList<>();
        BlockVersionChannel blockVersionChannel = mHdpRepository.getBlockVersionChannel();
        if (blockVersionChannel != null) {
            List<Integer> blockChannels = blockVersionChannel.getChannelNums();
            List<String> streamPregs = blockVersionChannel.getStreamPregs();
            if (blockChannels != null && !blockChannels.isEmpty() && streamPregs != null && !streamPregs.isEmpty()) {
                if (blockChannels.contains(channelModel.getNum())) {
                    if (hdpApi.getCurrentEpgIsBlocked(channelModel.getEpgId())) {
                        for (String url : channelModel.getUrls()) {
                            for (String streamPreg : streamPregs) {
                                try {
                                    if (Pattern.compile(streamPreg).matcher(url).find()) {
                                        blockUrls.add(url);
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        return blockUrls;
    }

    /**
     * 判断地域时候匹配
     * 其中屏蔽地域为空是则代表屏蔽所有地域
     */
    private boolean isRegionMatched(BlockInfoModel blockInfoModel) {
        List<String> regions = blockInfoModel.getRegions();
        HdpApi hdpApi = mPluginLoader.createApi(HdpApi.class);
        if (regions == null || regions.isEmpty() || hdpApi == null || TextUtils.isEmpty(hdpApi.getRegion())) {
            return true;
        } else {
            for (String region : regions) {
                if (hdpApi.getRegion().contains(region)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 判断视屏时间是被匹配
     */
    private boolean isTimeMatched(BlockInfoModel blockInfoModel) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        for (BlockTimesModel blockTimesModel : blockInfoModel.getBtimes()) {
            if (currentTimeSeconds >= blockTimesModel.getStart() && currentTimeSeconds <= blockTimesModel.getEnd()) {
                return true;
            }
        }
        return false;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public boolean isInBlockTime() {
        return isInBlockTime(channelNum);
    }
}
