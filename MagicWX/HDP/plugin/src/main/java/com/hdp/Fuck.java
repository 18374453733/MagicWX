package com.hdp;

import android.content.Context;

import java.io.File;

public class Fuck {

    private volatile static boolean isLoaded;

    public static String g(Context context, String id) {
        if (!isLoaded) {
            synchronized (Fuck.class) {
                if (!isLoaded) {
                    File libraryFile = new File(context.getDir("libs", Context.MODE_PRIVATE), "libfuck.so");
                    if (libraryFile.exists()) {
                        System.load(libraryFile.getAbsolutePath());
                        isLoaded = true;
                    }
                }
            }
        }
        if (isLoaded) {
            return a(id);
        } else {
            return null;
        }
    }

    private static native String a(String id);

}
