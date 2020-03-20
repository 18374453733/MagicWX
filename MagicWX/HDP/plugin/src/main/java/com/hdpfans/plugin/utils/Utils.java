package com.hdpfans.plugin.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class Utils {

    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    public static String md5(String input) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(input.getBytes());
            return new BigInteger(1, localMessageDigest.digest())
                    .toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isHdp(Context context) {
        return context.getPackageName().equals("hdpfans.com");
    }

    public static String buildFlavor(Context context) {
        try {
            Class buildConfig = Class.forName(context.getPackageName() + ".BuildConfig");
            Object instance = buildConfig.newInstance();
            Field flavor = buildConfig.getDeclaredField("FLAVOR");
            flavor.setAccessible(true);
            return (String) flavor.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String macAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if (wifiInf != null && marshmallowMacAddress.equals(wifiInf.getMacAddress())) {
            String result = null;
            try {
                result = getAddressMacByInterface();
                if (result != null) {
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    private static String getAddressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }

    public static String androidID(Context context) {
        String idsDevice = "";
        try {
            idsDevice = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(idsDevice)) {
                idsDevice = System.currentTimeMillis() / 1000 + "" + System.nanoTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return idsDevice;
    }

    public static String getOsKey(Context context) {
        String osKey = "";
        osKey = getAndroid(context, osKey);
        osKey = getSerial(osKey);
        osKey = getIme(context, osKey);
        String model = android.os.Build.MODEL + "_"
                + android.os.Build.MANUFACTURER;
        int ver = android.os.Build.VERSION.SDK_INT;
        osKey = osKey + "[" + model + "]-[" + ver + "]";
        osKey = md5(osKey);
        return osKey;
    }

    private static String getAndroid(Context context, String x) {
        try {
            return x + Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (Exception e) {
            return x;
        }
    }

    private static String getSerial(String x) {
        try {
            return x + Build.SERIAL;
        } catch (Exception e) {
            return x;
        }
    }

    private static String getIme(Context context, String x) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            return x + tm.getDeviceId();
        } catch (Exception e) {
            return x;
        }
    }

    public static String getUserAgent() {
        String userAgent = System.getProperty("http.agent");
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = "hdp_user";
        }
        return userAgent;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
