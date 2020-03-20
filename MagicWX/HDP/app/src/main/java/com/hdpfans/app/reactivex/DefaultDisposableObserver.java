package com.hdpfans.app.reactivex;


import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import com.hdpfans.app.exception.ErrorMessageFactory;

public class DefaultDisposableObserver<T> extends DisposableObserver<T> {

    @Override
    public void onNext(@NonNull T t) {
        // no-op by default
    }

    @Override
    public void onError(@NonNull Throwable e) {
        ErrorMessageFactory.create((Exception) e);
    }

    @Override
    public void onComplete() {
        // no-op by default
    }
}
