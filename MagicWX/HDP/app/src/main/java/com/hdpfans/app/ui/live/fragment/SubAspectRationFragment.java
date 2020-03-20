package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.Settings;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.widget.ElementView;
import com.hdpfans.app.ui.widget.media.IRenderView;

import butterknife.BindView;
import butterknife.OnClick;
import hdpfans.com.R;

public class SubAspectRationFragment extends SettingFragment {

    public static SubAspectRationFragment newInstance() {
        return new SubAspectRationFragment();
    }

    private Settings mSettings;

    @BindView(R.id.btn_menu_ar_fit_parent)
    ElementView mBntArFitParent;
    @BindView(R.id.btn_menu_ar_full_parent)
    ElementView mBntArFullPrent;
    @BindView(R.id.btn_menu_ar_16_9)
    ElementView mBtnAr16_9;
    @BindView(R.id.btn_menu_ar_4_3)
    ElementView mBtnAR4_3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_aspect_ration, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSettings = new Settings(getContext());
        initSelect(mSettings.getAspectRatio());
    }

    private void initSelect(int ratio) {
        mBntArFitParent.isChecked(false);
        mBntArFullPrent.isChecked(false);
        mBtnAr16_9.isChecked(false);
        mBtnAR4_3.isChecked(false);
        switch (ratio){
            case IRenderView.AR_ASPECT_FIT_PARENT:
                mBntArFitParent.isChecked(true);
                break;
            case IRenderView.AR_ASPECT_FILL_PARENT:
                mBntArFullPrent.isChecked(true);
                break;
            case IRenderView.AR_16_9_FIT_PARENT:
                mBtnAr16_9.isChecked(true);
                break;
            case IRenderView.AR_4_3_FIT_PARENT:
                mBtnAR4_3.isChecked(true);
                break;
        }
    }

    @OnClick({R.id.btn_menu_ar_fit_parent, R.id.btn_menu_ar_full_parent, R.id.btn_menu_ar_16_9, R.id.btn_menu_ar_4_3})
    void onClickAspectRation(View view) {
        switch (view.getId()) {
            case R.id.btn_menu_ar_fit_parent:
                mSettings.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                break;
            case R.id.btn_menu_ar_full_parent:
                mSettings.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
                break;
            case R.id.btn_menu_ar_16_9:
                mSettings.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
                break;
            case R.id.btn_menu_ar_4_3:
                mSettings.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
                break;
        }
        initSelect(mSettings.getAspectRatio());
        onSettingChanged(MenuActivity.OPERATING_CODE_TOGGLE_RATIO);
    }
}
