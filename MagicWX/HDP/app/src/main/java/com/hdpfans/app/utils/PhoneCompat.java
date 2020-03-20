package com.hdpfans.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hdpfans.app.App;
import com.hdpfans.app.ui.main.MainActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.annotation.Nonnull;

public class PhoneCompat {

    public static int px2dp(@Nonnull Context context, float pxValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics());
    }

    public static int px2sp(@Nonnull Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int dp2px(@Nonnull Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(@Nonnull Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int phoneWidth(@Nonnull Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int phoneHeight(@Nonnull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static void hideKeyboard(@Nonnull Activity activity) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    public static void showKeyboard(@Nonnull Activity activity, @Nonnull View view) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static boolean isWifi(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String getWifiIpAddress(@NonNull Context context) {
        try {
            NetworkInfo info = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info != null && info.isConnected() && (info.getType() == ConnectivityManager.TYPE_WIFI)) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intIP2StringIP(wifiInfo.getIpAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLanIpAddress(@NonNull Context context) {
        if (isWifi(context)) {
            return getWifiIpAddress(context);
        } else {
            return getRJ45Ip();
        }
    }

    public static String getRJ45Ip() {
        try {
            Enumeration<NetworkInterface> ems = NetworkInterface.getNetworkInterfaces();
            while (ems.hasMoreElements()) {
                NetworkInterface networkInterface = ems.nextElement();
                String itfName = networkInterface.getDisplayName();
                if (itfName.equals("eth0")) {
                    Enumeration<InetAddress> et = networkInterface.getInetAddresses();
                    while (et.hasMoreElements()) {
                        InetAddress inetAddress = et.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    public static boolean isInstallPackage(@NonNull Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        return packageInfo != null;

    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static void restartApp(Context context) {
        Intent it = new Intent(context, MainActivity.class);
        it.setClassName(context.getPackageName(), MainActivity.class.getName());
        it.putExtra("ChannelNum", 1);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }

    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static void exit(Context context) {
        ((App) context.getApplicationContext()).exit();
        System.exit(0);
    }
}
