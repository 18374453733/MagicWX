package com.hdpfans.app.model.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hdpfans.app.model.annotation.BootChannelMode.COLLECT;
import static com.hdpfans.app.model.annotation.BootChannelMode.DEFAULT;
import static com.hdpfans.app.model.annotation.BootChannelMode.LAST_WATCH;

@IntDef({DEFAULT, COLLECT, LAST_WATCH})
@Retention(RetentionPolicy.SOURCE)
public @interface BootChannelMode {

    int DEFAULT = 0;

    int COLLECT = 1;

    int LAST_WATCH = 2;
}
