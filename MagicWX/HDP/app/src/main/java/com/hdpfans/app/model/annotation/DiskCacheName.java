package com.hdpfans.app.model.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hdpfans.app.model.annotation.DiskCacheName.IMAGE;
import static com.hdpfans.app.model.annotation.DiskCacheName.TMP;

@StringDef({IMAGE, TMP})
@Retention(RetentionPolicy.SOURCE)
public @interface DiskCacheName {

    String IMAGE = "image";

    String TMP = "tmp";

}
