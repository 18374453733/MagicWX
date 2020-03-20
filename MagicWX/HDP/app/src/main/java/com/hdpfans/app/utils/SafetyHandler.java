package com.hdpfans.app.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class SafetyHandler extends Handler {
    private final WeakReference<Delegate> mWeakReference;

    private SafetyHandler() {
        this(null);
    }

    private SafetyHandler(Delegate delegate) {
        this.mWeakReference = new WeakReference<>(delegate);
    }

    public static SafetyHandler create(Delegate delegate) {
        return new SafetyHandler(delegate);
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (this.mWeakReference != null && this.mWeakReference.get() != null) {
            this.mWeakReference.get().onReceivedHandlerMessage(msg);
        }
    }


    public interface Delegate {
        void onReceivedHandlerMessage(Message message);
    }
}