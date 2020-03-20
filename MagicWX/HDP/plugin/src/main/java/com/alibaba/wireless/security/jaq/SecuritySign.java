package com.alibaba.wireless.security.jaq;

import android.content.Context;
import android.os.Build;

import com.hdp.Fuck;

import java.io.File;

public class SecuritySign {

    private volatile static boolean isLoaded;

    public static void i(Context context) {
        if (!isLoaded) {
            synchronized (Fuck.class) {
                if (!isLoaded) {
                    File libraryFile = new File(context.getDir("libs", Context.MODE_PRIVATE), "libsign.so");
                    if (libraryFile.exists()) {
                        System.load(libraryFile.getAbsolutePath());
                        init(context, "android", Build.VERSION.SDK_INT, Build.MODEL.replaceAll("\\s+", "_"));
                        isLoaded = true;
                    }
                }
            }
        }
    }

    public static String g(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
        try {
            if (!isLoaded) {
                return null;
            }
            return sign(paramString1, paramString2, paramString3, paramString4, paramLong);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static native void init(Context paramContext, String paramString1, int paramInt, String paramString2);

    private static native String sign(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong);

}
