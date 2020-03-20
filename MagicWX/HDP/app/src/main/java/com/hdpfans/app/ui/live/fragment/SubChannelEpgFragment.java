package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.widget.ElementView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

public class SubChannelEpgFragment extends SettingFragment {

    public static SubChannelEpgFragment newInstance() {
        return new SubChannelEpgFragment();
    }

    @Inject
    PrefManager mPrefManager;

    @BindView(R.id.btn_epg_open)
    ElementView mBtnEpgOpen;
    @BindView(R.id.btn_epg_close)
    ElementView mBtnEpgClose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_channel_epg, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSelect(mPrefManager.isOpenChannelEpg());
    }

    private void initSelect(boolean b) {
        mBtnEpgOpen.isChecked(b);
        mBtnEpgClose.isChecked(!b);
    }

    @OnClick({R.id.btn_epg_open, R.id.btn_epg_close})
    void onClickBootModel(View view) {
        switch (view.getId()) {
            case R.id.btn_epg_open:
                mPrefManager.setOpenChannelEpg(true);
                break;
            case R.id.btn_epg_close:
                mPrefManager.setOpenChannelEpg(false);
                break;
        }
        initSelect(mPrefManager.isOpenChannelEpg());
        onSettingChanged(MenuActivity.OPERATING_CODE_EPG);
    }
}
