package com.hdpfans.app.model.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hdpfans.app.model.annotation.ChannelUpdateType.DELETE;
import static com.hdpfans.app.model.annotation.ChannelUpdateType.INSERT;
import static com.hdpfans.app.model.annotation.ChannelUpdateType.UPDATE;

/**
 * 节目信息增量更新规则
 */
@IntDef({INSERT, DELETE, UPDATE})
@Retention(RetentionPolicy.SOURCE)
public @interface ChannelUpdateType {

    int INSERT = 1;

    int DELETE = 2;

    int UPDATE = 3;

}
