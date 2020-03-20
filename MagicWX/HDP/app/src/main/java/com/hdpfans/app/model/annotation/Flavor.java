package com.hdpfans.app.model.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hdpfans.app.model.annotation.Flavor.CHUANG_WEI;
import static com.hdpfans.app.model.annotation.Flavor.DANG_BEI;
import static com.hdpfans.app.model.annotation.Flavor.HDP;
import static com.hdpfans.app.model.annotation.Flavor.SHA_FA;

/**
 * 常用渠道名称
 */
@StringDef({HDP, DANG_BEI, SHA_FA, CHUANG_WEI})
@Retention(RetentionPolicy.SOURCE)
public @interface Flavor {

    /**
     * 官方渠道
     */
    String HDP = "hdp";

    /**
     * 当贝
     */
    String DANG_BEI = "dangbei";

    /**
     * 沙发
     */
    String SHA_FA = "shafa";

    /**
     * 创维
     */
    String CHUANG_WEI = "chuangwei";

}
