package com.hdpfans.app.reactivex;

import io.reactivex.observers.DisposableSingleObserver;
import com.hdpfans.app.exception.ErrorMessageFactory;

public class DefaultDisposableSingleObserver<T> extends DisposableSingleObserver<T> {
    @Override
    public void onSuccess(T t) {
        // no-op by default
    }

    @Override
    public void onError(Throwable e) {
        ErrorMessageFactory.create((Exception) e);
    }
}
