package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import butterknife.OnFocusChange;
import hdpfans.com.R;

public class OperatingSettingFragment extends SettingFragment {

    public static OperatingSettingFragment newInstance() {
        return new OperatingSettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operating_setting, container, false);
    }

    @OnFocusChange(R.id.btn_menu_voice_support)
    void onFocusVoiceSupport(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubVoiceSupportFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_voice_support)
    void onClickVoiceSupport() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubVoiceSupportFragment.newInstance()).commitAllowingStateLoss();
    }

    @OnFocusChange(R.id.btn_menu_system_boot)
    void onFocusSystemBoot(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubSystemBootFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_system_boot)
    void onClickSystemBoot() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubSystemBootFragment.newInstance()).commitAllowingStateLoss();
    }
}
