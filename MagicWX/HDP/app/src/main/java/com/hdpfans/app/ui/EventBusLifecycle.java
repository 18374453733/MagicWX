package com.hdpfans.app.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import org.greenrobot.eventbus.EventBus;

import javax.annotation.Nonnull;

public class EventBusLifecycle implements LifecycleObserver {

    private Object context;

    public EventBusLifecycle(@Nonnull Object context) {
        this.context = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void register() {
        EventBus.getDefault().register(context);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void unregister() {
        EventBus.getDefault().unregister(context);
    }

}
