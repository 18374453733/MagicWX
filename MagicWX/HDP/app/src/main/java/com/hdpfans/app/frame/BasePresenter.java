package com.hdpfans.app.frame;

import android.arch.lifecycle.LifecycleObserver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class BasePresenter<V extends BaseView> implements LifecycleObserver, IPresenter<V> {

    private V view;
    private Context context;
    private Intent intent;
    private Bundle arguments;

    @Override
    public void attachView(V view) {
        this.view = view;
    }

    @Override
    final public V getView() {
        return view;
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(Context content) {
        this.context = content.getApplicationContext();
    }

    @Override
    public Intent getIntent() {
        return intent;
    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public Bundle getArguments() {
        return arguments;
    }

    @Override
    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }
}
