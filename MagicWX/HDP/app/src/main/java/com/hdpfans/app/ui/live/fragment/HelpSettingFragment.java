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
public class HelpSettingFragment extends SettingFragment {

    public static HelpSettingFragment newInstance() {
        return new HelpSettingFragment();
    }

    @Inject
    HdpRepository mHdpRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_setting, container, false);
    }

    @OnClick(R.id.btn_show_help)
    void onClickShowHelp() {
        getActivity().startActivity(HdpTipsActivity.navigateToTipsActivity(getActivity(), mHdpRepository.getHelpImage()));
        onSettingChanged(MenuActivity.OPERATING_CODE_HELP, true);
    }
}
