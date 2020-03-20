package com.hdpfans.app.utils;

import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReactivexCompat {

    public static <T> ObservableTransformer<T, T> observableTransform() {
        return upstream -> upstream.compose(observableThreadSchedule());
    }

    public static <T> SingleTransformer<T, T> singleTransform() {
        return upstream -> upstream.compose(singleThreadSchedule());
    }

    public static <T> MaybeTransformer<T, T> maybeThreadSchedule() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> singleThreadSchedule() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static CompletableTransformer completableThreadSchedule() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> observableThreadSchedule() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> flowableThreadSchedule() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
