package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.ui.live.MenuActivity;

import butterknife.OnClick;
import hdpfans.com.R;

public class ChannelManagerSettingFragment extends SettingFragment {

    public static ChannelManagerSettingFragment newInstance() {
        return new ChannelManagerSettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel_manager_setting, container, false);
    }

    @OnClick(R.id.btn_channel_manager)
    void OnClickChannelManager() {
        onSettingChanged(MenuActivity.OPERATING_CODE_CHANNEL_MANAGER, true);
    }
}
