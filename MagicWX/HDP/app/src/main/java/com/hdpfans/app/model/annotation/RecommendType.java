package com.hdpfans.app.model.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({RecommendType.APK, RecommendType.CHANNEL})
@Retention(RetentionPolicy.SOURCE)
public @interface RecommendType {

    int APK = 1;

    int CHANNEL = 2;
}
