package com.hdpfans.app.ui.live.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.data.manager.FileManager;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.utils.PhoneCompat;

import javax.inject.Inject;

import butterknife.OnClick;
import hdpfans.com.R;

public class OtherSettingFragment extends SettingFragment {

    public static OtherSettingFragment newInstance() {
        return new OtherSettingFragment();
    }

    @Inject
    FileManager mFileManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_setting, container, false);
    }

    @OnClick(R.id.btn_clear_cache)
    void onClickClearCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.clear_cache);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            try {
                onSettingChanged(MenuActivity.OPERATING_CODE_CLEAR_CACHE);
                mFileManager.deleteFolderFile(getContext().getCacheDir().getAbsolutePath(), false);
                PackageManager pm = getContext().getPackageManager();
                Intent it = pm.getLaunchIntentForPackage(getContext().getPackageName());
                if (null != it) {
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(it);
                }
                dialog.dismiss();
                PhoneCompat.restartApp(getContext());
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
