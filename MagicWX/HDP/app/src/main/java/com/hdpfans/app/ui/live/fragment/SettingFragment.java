package com.hdpfans.app.ui.live.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.frame.FrameFragment;
import com.hdpfans.app.ui.live.LivePlayActivity;

import static com.hdpfans.app.ui.live.MenuActivity.RESULT_RESULT_OPERATING_CODE;

public abstract class SettingFragment extends FrameFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            if (childCount == 1) {
                View child = ((ViewGroup) view).getChildAt(0);
                shieldKeyEvent(child, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN);
            }

            if (childCount > 1) {
                View firstView = ((ViewGroup) view).getChildAt(0);
                if (childCount == 2 && firstView instanceof ViewGroup) {
                    int count = ((ViewGroup) firstView).getChildCount();
                    if (count > 0) {
                        shieldKeyEvent(((ViewGroup) firstView).getChildAt(0), KeyEvent.KEYCODE_DPAD_UP);
                        shieldKeyEvent(((ViewGroup) firstView).getChildAt(count - 1), KeyEvent.KEYCODE_DPAD_DOWN);
                    }
                }
                shieldKeyEvent(firstView, KeyEvent.KEYCODE_DPAD_UP);
                shieldKeyEvent(((ViewGroup) view).getChildAt(childCount - 1), KeyEvent.KEYCODE_DPAD_DOWN);
            }
        }
    }

    private void shieldKeyEvent(View view, int... keyCodes) {
        view.setOnKeyListener((view1, i, keyEvent) -> {
            for (int keyCode : keyCodes) {
                if (keyEvent.getKeyCode() == keyCode) {
                    return true;
                }
            }
            return false;
        });
    }

    protected void onSettingChanged(int code) {
        onSettingChanged(code, false);
    }

    protected void onSettingChanged(int code, boolean finish) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_RESULT_OPERATING_CODE, code);
        if (finish) {
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            intent.setAction(LivePlayActivity.ACTION_OPERATING_MENU);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        }
    }
}
