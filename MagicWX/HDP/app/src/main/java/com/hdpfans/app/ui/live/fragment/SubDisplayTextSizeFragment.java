package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.model.annotation.DisplayTextSizeMode;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.widget.ElementView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

public class SubDisplayTextSizeFragment extends SettingFragment {

    public static SubDisplayTextSizeFragment newInstance() {
        return new SubDisplayTextSizeFragment();
    }

    @Inject
    PrefManager mPrefManager;

    @BindView(R.id.btn_txt_size_big)
    ElementView mBtnTxtSizeBig;
    @BindView(R.id.btn_txt_size_middle)
    ElementView mBtnTxtSizeMiddle;
    @BindView(R.id.btn_txt_size_small)
    ElementView mBtnTxtSizeSmall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_text_size, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSelect(mPrefManager.getDisplayTextSizeMode());
    }

    private void initSelect(int b) {
        mBtnTxtSizeMiddle.isChecked(false);
        mBtnTxtSizeBig.isChecked(false);
        mBtnTxtSizeSmall.isChecked(false);
        switch (b) {
            case DisplayTextSizeMode.MIDDLE:
                mBtnTxtSizeMiddle.isChecked(true);
                break;
            case DisplayTextSizeMode.BIG:
                mBtnTxtSizeBig.isChecked(true);
                break;
            case DisplayTextSizeMode.SMALL:
                mBtnTxtSizeSmall.isChecked(true);
                break;
        }
    }

    @OnClick({R.id.btn_txt_size_middle, R.id.btn_txt_size_big, R.id.btn_txt_size_small})
    void onClickTextSizeMode(View view) {
        switch (view.getId()) {
            case R.id.btn_txt_size_middle:
                mPrefManager.setDisplayTextSizeMode(DisplayTextSizeMode.MIDDLE);
                break;
            case R.id.btn_txt_size_big:
                mPrefManager.setDisplayTextSizeMode(DisplayTextSizeMode.BIG);
                break;
            case R.id.btn_txt_size_small:
                mPrefManager.setDisplayTextSizeMode(DisplayTextSizeMode.SMALL);
                break;
        }
        initSelect(mPrefManager.getDisplayTextSizeMode());
        onSettingChanged(MenuActivity.OPERATING_CODE_TEXT_SIZE);
    }
}
