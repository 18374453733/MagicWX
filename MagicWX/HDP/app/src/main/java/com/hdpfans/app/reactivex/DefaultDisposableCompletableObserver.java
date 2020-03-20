package com.hdpfans.app.reactivex;

import io.reactivex.observers.DisposableCompletableObserver;
import com.hdpfans.app.exception.ErrorMessageFactory;

public class DefaultDisposableCompletableObserver extends DisposableCompletableObserver {
    @Override
    public void onComplete() {
        // no-op by default
    }

    @Override
    public void onError(Throwable e) {
        ErrorMessageFactory.create((Exception) e);
    }
}
