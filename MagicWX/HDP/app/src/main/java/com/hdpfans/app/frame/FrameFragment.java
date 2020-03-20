package com.hdpfans.app.frame;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.internal.Beta;
import com.hdpfans.app.utils.PresenterCompat;

@Beta
public abstract class FrameFragment extends Fragment implements HasFragmentInjector, BaseView {

    @Inject
    DispatchingAndroidInjector<android.app.Fragment> childFragmentInjector;

    private final LifecycleProvider<Lifecycle.Event> provider
            = AndroidLifecycle.createLifecycleProvider(this);

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        try {
            AndroidSupportInjection.inject(this);
        } catch (Exception ignored) {
        }
        super.onAttach(context);
        PresenterCompat.inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        // 存在presenter onCreate调用，保证View已经初始化
        List<BasePresenter> presenters = PresenterCompat.getPresenters(this);
        for (BasePresenter presenter : presenters) {
            getLifecycle().addObserver(presenter);
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public AndroidInjector<android.app.Fragment> fragmentInjector() {
        return childFragmentInjector;
    }

    @Override
    public LifecycleProvider<Lifecycle.Event> getLifecycleProvider() {
        return provider;
    }

    @Override
    public void toast(CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
