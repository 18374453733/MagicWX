package com.hdpfans.app.model.entity;

import java.util.List;

public class FlavorRecommend {

    private List<SpecialFlavorRecommend> specialFlavor;

    private DefaultFlavorRecommend defaultFlavor;

    public List<SpecialFlavorRecommend> getSpecialFlavor() {
        return specialFlavor;
    }

    public void setSpecialFlavor(List<SpecialFlavorRecommend> specialFlavor) {
        this.specialFlavor = specialFlavor;
    }

    public DefaultFlavorRecommend getDefaultFlavor() {
        return defaultFlavor;
    }

    public void setDefaultFlavor(DefaultFlavorRecommend defaultFlavor) {
        this.defaultFlavor = defaultFlavor;
    }
}
