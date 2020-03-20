package com.hdpfans.app.frame;

import android.arch.lifecycle.Lifecycle;

import com.trello.rxlifecycle2.LifecycleProvider;

public interface BaseView {

    LifecycleProvider<Lifecycle.Event> getLifecycleProvider();

    void toast(CharSequence message);

}
