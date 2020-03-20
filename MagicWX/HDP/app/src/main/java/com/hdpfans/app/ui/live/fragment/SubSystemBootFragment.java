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

public class SubSystemBootFragment extends SettingFragment {

    public static SubSystemBootFragment newInstance() {
        return new SubSystemBootFragment();
    }

    @Inject
    PrefManager mPrefManager;

    @BindView(R.id.btn_system_boot_open)
    ElementView mBtnSystemBootOpen;
    @BindView(R.id.btn_system_boot_close)
    ElementView mBtnSystemBootClose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_system_boot, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSelect(mPrefManager.isOpenSystemBoot());
    }


    private void initSelect(boolean b) {
        mBtnSystemBootOpen.isChecked(b);
        mBtnSystemBootClose.isChecked(!b);
    }

    @OnClick({R.id.btn_system_boot_open, R.id.btn_system_boot_close})
    void onClickSystemBoot(View view) {
        switch (view.getId()) {
            case R.id.btn_system_boot_open:
                mPrefManager.setOpenSystemBoot(true);
                break;
            case R.id.btn_system_boot_close:
                mPrefManager.setOpenSystemBoot(false);
                break;
        }
        initSelect(mPrefManager.isOpenSystemBoot());
        onSettingChanged(MenuActivity.OPERATING_CODE_SYSTEM_BOOT);
    }
}
