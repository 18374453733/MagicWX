package com.hdpfans.app.frame;

import android.app.Fragment;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Toast;

import hdpfans.com.R;
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
import com.hdpfans.app.utils.PresenterCompat;

public abstract class FrameBottomSheetFragment extends BottomSheetDialogFragment
        implements HasFragmentInjector, BaseView {

    private final LifecycleProvider<Lifecycle.Event> provider
            = AndroidLifecycle.createLifecycleProvider(this);

    @Inject
    DispatchingAndroidInjector<Fragment> childFragmentInjector;

    private Unbinder unbinder;
    private BottomSheetBehavior mBehavior;

    @Override
    public void onAttach(Context context) {
        try {
            AndroidSupportInjection.inject(this);
        } catch (Exception ignored) {
        }
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        PresenterCompat.inject(this);
        // 存在presenter onCreate调用，保证View已经初始化
        List<BasePresenter> presenters = PresenterCompat.getPresenters(this);
        for (BasePresenter presenter : presenters) {
            getLifecycle().addObserver(presenter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBehavior == null) {
            View coordinator = getDialog().findViewById(R.id.design_bottom_sheet);
            mBehavior = BottomSheetBehavior.from(coordinator);
        }
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return childFragmentInjector;
    }

    @Override
    public LifecycleProvider<Lifecycle.Event> getLifecycleProvider() {
        return provider;
    }

    @Override
    public void toast(CharSequence message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
