package com.hdpfans.app.receiver;

import android.content.Context;
import android.content.Intent;

import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.ui.main.MainActivity;

import javax.inject.Inject;

import dagger.android.DaggerBroadcastReceiver;

/**
 * 开机启动监听
 */
public class BootBroadcastReceiver extends DaggerBroadcastReceiver {

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Inject
    PrefManager mPrefManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (mPrefManager.isOpenSystemBoot() && ACTION.equals(intent.getAction())) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
