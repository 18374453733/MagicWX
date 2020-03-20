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

public class SubVoiceSupportFragment extends SettingFragment {

    public static SubVoiceSupportFragment newInstance() {
        return new SubVoiceSupportFragment();
    }

    @Inject
    PrefManager mPrefManager;

    @BindView(R.id.btn_voice_support_open)
    ElementView mBtnVoiceSupportOpen;
    @BindView(R.id.btn_voice_support_close)
    ElementView mBtnVoiceSupportClose;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_voice_support, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSelect(mPrefManager.isOpenVoiceSupport());
    }

    private void initSelect(boolean b) {
        mBtnVoiceSupportOpen.isChecked(b);
        mBtnVoiceSupportClose.isChecked(!b);
    }


    @OnClick({R.id.btn_voice_support_open, R.id.btn_voice_support_close})
    void onClickVoiceSupport(View view) {
        switch (view.getId()) {
            case R.id.btn_voice_support_open:
                mPrefManager.setOpenVoiceSupport(true);
                break;
            case R.id.btn_voice_support_close:
                mPrefManager.setOpenVoiceSupport(false);
                break;
        }
        initSelect(mPrefManager.isOpenVoiceSupport());
        onSettingChanged(MenuActivity.OPERATING_CODE_VOICE_SUPPORT);
    }
}
