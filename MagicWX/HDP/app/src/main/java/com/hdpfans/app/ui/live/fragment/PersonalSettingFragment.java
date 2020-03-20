package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import hdpfans.com.R;

public class PersonalSettingFragment extends SettingFragment {

    public static PersonalSettingFragment newInstance() {
        return new PersonalSettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_setting, container, false);
    }

    @OnClick(R.id.btn_show_personal)
    void onClickShowPersonal() {
        toast("暂未开放");
    }

}
