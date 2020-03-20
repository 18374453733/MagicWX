package com.hdpfans.plugin.compat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class BoxCompat {

    private static boolean sIsCheckType;
    private static boolean sIsPhoneRunCache;

    /**
     * 检查当前屏幕的物理尺寸, 小于6.5认为是手机，否则认为是盒子
     */
    private static boolean checkScreenIsPhone(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        double x = Math.pow(displayMetrics.widthPixels / displayMetrics.xdpi, 2);
        double y = Math.pow(displayMetrics.heightPixels / displayMetrics.ydpi, 2);

        double screenInches = Math.sqrt(x + y);
        return screenInches < 6.5;
    }

    /**
     * 检查如果当前设备的布局尺寸，如果SIZE_LARGE就认为是大屏幕
     */
    private static boolean checkScreenLayoutIsPhone(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                <= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 检查SIM卡的状态，如果没有检测到，任务是盒子
     */
    private static boolean checkTelephonyIsPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * 检查当前电源的输入状态，来判断当前是盒子还是手机
     */
    private static boolean checkBatteryIsPhone(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        // 当前电池的状态
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_FULL;

        // 当前充电的状态
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // 盒子的状态：当前电量一定是满的，并且AC交流电接入才认为是盒子
        return !(isCharging && acCharge);
    }

    public static boolean isPhoneRunning(Context context) {
        if (!sIsCheckType) {
            sIsPhoneRunCache = checkScreenIsPhone(context)
                    && checkScreenLayoutIsPhone(context)
                    && checkTelephonyIsPhone(context)
                    && checkBatteryIsPhone(context);
            sIsCheckType = true;
        }
        return sIsPhoneRunCache;
    }

}
