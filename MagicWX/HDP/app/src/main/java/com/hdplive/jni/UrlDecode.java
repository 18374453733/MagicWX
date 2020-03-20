package com.hdplive.jni;

public class UrlDecode {
    static {
        System.loadLibrary("UrlDecode");
    }

    public static native String decode(Object context, String str);

}
