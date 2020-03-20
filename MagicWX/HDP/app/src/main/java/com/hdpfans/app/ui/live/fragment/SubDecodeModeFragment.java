package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.Settings;
import com.hdpfans.app.model.annotation.MediaCodecType;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.widget.ElementView;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

public class SubDecodeModeFragment extends SettingFragment {

    public static SubDecodeModeFragment newInstance() {
        return new SubDecodeModeFragment();
    }

    private Settings mSettings;

    @BindView(R.id.btn_decode_mode_ss)
    ElementView mBntDecodeModeSS;
    @BindView(R.id.btn_decode_mode_hs)
    ElementView mBntDecodeModeHS;
    @BindView(R.id.btn_decode_mode_smart)
    ElementView mBtnDecodeModeSmart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_decode_mode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSettings = new Settings(getContext());

        initSelect(mSettings.getUsingMediaCodec());
    }

    private void initSelect(int b) {
        mBtnDecodeModeSmart.isChecked(false);
        mBntDecodeModeHS.isChecked(false);
        mBntDecodeModeSS.isChecked(false);
        switch (b) {
            case MediaCodecType.SMART:
                mBtnDecodeModeSmart.isChecked(true);
                break;
            case MediaCodecType.HARD:
                mBntDecodeModeHS.isChecked(true);
                break;
            case MediaCodecType.SOFT:
                mBntDecodeModeSS.isChecked(true);
                break;
        }
    }

    @OnClick({R.id.btn_decode_mode_smart, R.id.btn_decode_mode_hs, R.id.btn_decode_mode_ss})
    void onClickDecodeModes(View view) {
        switch (view.getId()) {
            case R.id.btn_decode_mode_smart:
                mSettings.setUsingMediaCodec(MediaCodecType.SMART);
                break;
            case R.id.btn_decode_mode_hs:
                mSettings.setUsingMediaCodec(MediaCodecType.HARD);
                break;
            case R.id.btn_decode_mode_ss:
                mSettings.setUsingMediaCodec(MediaCodecType.SOFT);
        }
        initSelect(mSettings.getUsingMediaCodec());
        onSettingChanged(MenuActivity.OPERATING_CODE_TOGGLE_PLAYER);
    }
}
