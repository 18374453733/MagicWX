package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.main.HdpTipsActivity;

import javax.inject.Inject;

import butterknife.OnClick;
import hdpfans.com.R;

@ActivityScope
public class FeatureSettingFragment extends SettingFragment {

    public static FeatureSettingFragment newInstance() {
        return new FeatureSettingFragment();
    }

    @Inject
    HdpRepository mHdpRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feature_setting, container, false);
    }

    @OnClick(R.id.btn_show_feature)
    void onClickShowFeature() {
        getActivity().startActivity(HdpTipsActivity.navigateToTipsActivity(getActivity(), mHdpRepository.getFeatureImage()));
        onSettingChanged(MenuActivity.OPERATING_CODE_FEATURE, true);
    }
}
