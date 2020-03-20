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

public class MainSettingFragment extends SettingFragment {

    public static MainSettingFragment newInstance() {
        return new MainSettingFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_setting, container, false);
    }

    @OnFocusChange(R.id.btn_menu_decode_mode)
    void onFocusDecodeMode(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubDecodeModeFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_decode_mode)
    void onClickDecodeMode() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubDecodeModeFragment.newInstance()).commitAllowingStateLoss();
    }

    @OnFocusChange(R.id.btn_menu_aspect_ratio)
    void onFocusAspectRation(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubAspectRationFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_aspect_ratio)
    void onClickAspectRation() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubAspectRationFragment.newInstance()).commitAllowingStateLoss();
    }

    @OnFocusChange(R.id.btn_menu_region_channel)
    void onFocusRegionChannel(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubRegionChannelFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_region_channel)
    void onClickRegionChannel() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubRegionChannelFragment.newInstance()).commitAllowingStateLoss();
    }

    @OnFocusChange(R.id.btn_menu_boot_channel)
    void onFocusBootChannel(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubBootChannelFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_boot_channel)
    void onClickBootChannel() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubBootChannelFragment.newInstance()).commitAllowingStateLoss();
    }

    @OnFocusChange(R.id.btn_menu_display_text_size)
    void onFocusDisplayTextSize(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubDisplayTextSizeFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_display_text_size)
    void onClickDisplayTextSize() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubDisplayTextSizeFragment.newInstance()).commitAllowingStateLoss();
    }

    @OnFocusChange(R.id.btn_menu_channel_epg)
    void onFocusChannelEpg(boolean focus) {
        if (focus) {
            getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubChannelEpgFragment.newInstance()).commitAllowingStateLoss();
        }
    }

    @OnClick(R.id.btn_menu_channel_epg)
    void onClickChannelEpg() {
        getChildFragmentManager().beginTransaction().replace(R.id.layout_sub_menu_container, SubChannelEpgFragment.newInstance()).commitAllowingStateLoss();
    }
}
