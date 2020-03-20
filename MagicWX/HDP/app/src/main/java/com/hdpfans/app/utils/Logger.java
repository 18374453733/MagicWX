package com.hdpfans.app.utils;

import android.util.Log;

import hdpfans.com.BuildConfig;

public class Logger {
    private static final String LOG_PREFIX = "ARCH_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static boolean LOGGING_ENABLE = BuildConfig.DEBUG;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }


    public static void LOGD(final String tag, String message) {
        if (LOGGING_ENABLE) {
            Log.d(tag, message);
        }
    }

    public static void LOGV(final String tag, String message) {
        if (LOGGING_ENABLE) {
            Log.v(tag, message);
        }
    }

    public static void LOGI(final String tag, String message) {
        if (LOGGING_ENABLE) {
            Log.i(tag, message);
        }
    }

    public static void LOGW(final String tag, String message) {
        if (LOGGING_ENABLE) {
            Log.w(tag, message);
        }
    }

    public static void LOGE(final String tag, String message) {
        if (LOGGING_ENABLE) {
            Log.e(tag, message);
        }
    }
}
