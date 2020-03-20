package com.hdpfans.app.model.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hdpfans.app.model.annotation.QualityType.HD;
import static com.hdpfans.app.model.annotation.QualityType.SD;
import static com.hdpfans.app.model.annotation.QualityType.SHD;

@StringDef({SD, HD, SHD})
@Retention(RetentionPolicy.SOURCE)
public @interface QualityType {
    /**
     * 标清
     */
    String SD = "SD";

    /**
     * 高清
     */
    String HD = "HD";

    /**
     * 超清
     */
    String SHD = "SHD";
}
