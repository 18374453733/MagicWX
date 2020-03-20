package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.model.annotation.BootChannelMode;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.widget.ElementView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

public class SubBootChannelFragment extends SettingFragment {

    public static SubBootChannelFragment newInstance() {
        return new SubBootChannelFragment();
    }

    @Inject
    PrefManager mPrefManager;

    @BindView(R.id.btn_boot_default)
    ElementView mBtnBootDefault;
    @BindView(R.id.btn_boot_collect)
    ElementView mBtnBootCollect;
    @BindView(R.id.btn_boot_last_watch)
    ElementView mBtnBootLastWatch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_boot_channel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSelect(mPrefManager.getBootChannelMode());
    }


    private void initSelect(int b) {
        mBtnBootDefault.isChecked(false);
        mBtnBootCollect.isChecked(false);
        mBtnBootLastWatch.isChecked(false);
        switch (b) {
            case BootChannelMode.DEFAULT:
                mBtnBootDefault.isChecked(true);
                break;
            case BootChannelMode.COLLECT:
                mBtnBootCollect.isChecked(true);
                break;
            case BootChannelMode.LAST_WATCH:
                mBtnBootLastWatch.isChecked(true);
                break;
        }
    }

    @OnClick({R.id.btn_boot_default, R.id.btn_boot_collect, R.id.btn_boot_last_watch})
    void onClickBootMode(View view) {
        switch (view.getId()) {
            case R.id.btn_boot_default:
                mPrefManager.setBootChannelMode(BootChannelMode.DEFAULT);
                break;
            case R.id.btn_boot_collect:
                mPrefManager.setBootChannelMode(BootChannelMode.COLLECT);
                break;
            case R.id.btn_boot_last_watch:
                mPrefManager.setBootChannelMode(BootChannelMode.LAST_WATCH);
                break;
        }
        initSelect(mPrefManager.getBootChannelMode());
        onSettingChanged(MenuActivity.OPERATING_CODE_BOOT_CHANNEL);
    }
}
