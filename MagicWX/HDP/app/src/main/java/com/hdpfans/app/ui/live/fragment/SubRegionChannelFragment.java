package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.ui.widget.ElementView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

import static com.hdpfans.app.ui.live.MenuActivity.OPERATING_CODE_REGION_CHANNEL;

public class SubRegionChannelFragment extends SettingFragment {

    public static SubRegionChannelFragment newInstance() {
        return new SubRegionChannelFragment();
    }

    @Inject
    PrefManager mPrefManager;

    @BindView(R.id.btn_region_channel_hide)
    ElementView mBtnRegionChannelHide;
    @BindView(R.id.btn_region_channel_show)
    ElementView mBtnRegionChannelShow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_region_channel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSelect(mPrefManager.getRegionChannelVisibility());
    }

    private void initSelect(boolean b) {
        mBtnRegionChannelShow.isChecked(b);
        mBtnRegionChannelHide.isChecked(!b);
    }

    @OnClick({R.id.btn_region_channel_hide, R.id.btn_region_channel_show})
    void onClickRegionChannelVisibility(View view) {
        mPrefManager.setRegionChannelVisibility(view.getId() == R.id.btn_region_channel_show);
        initSelect(mPrefManager.getRegionChannelVisibility());
        onSettingChanged(OPERATING_CODE_REGION_CHANNEL);
    }
}
