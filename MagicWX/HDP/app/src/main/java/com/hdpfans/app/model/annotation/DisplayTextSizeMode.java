package com.hdpfans.app.model.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hdpfans.app.model.annotation.DisplayTextSizeMode.BIG;
import static com.hdpfans.app.model.annotation.DisplayTextSizeMode.MIDDLE;
import static com.hdpfans.app.model.annotation.DisplayTextSizeMode.SMALL;

@IntDef({MIDDLE, BIG, SMALL})
@Retention(RetentionPolicy.SOURCE)
public @interface DisplayTextSizeMode {

    int MIDDLE = 0;

    int BIG = 1;

    int SMALL = 2;
}
