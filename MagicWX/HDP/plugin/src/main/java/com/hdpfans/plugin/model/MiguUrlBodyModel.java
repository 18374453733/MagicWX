package com.hdpfans.plugin.model;

import java.util.List;

public class MiguUrlBodyModel {
    private MiguUrlInfoModel urlInfo;
    private List<MiguUrlInfoModel> urlInfos;

    public MiguUrlInfoModel getUrlInfo() {
        return urlInfo;
    }

    public void setUrlInfo(MiguUrlInfoModel urlInfo) {
        this.urlInfo = urlInfo;
    }

    public List<MiguUrlInfoModel> getUrlInfos() {
        return urlInfos;
    }

    public void setUrlInfos(List<MiguUrlInfoModel> urlInfos) {
        this.urlInfos = urlInfos;
    }
}
