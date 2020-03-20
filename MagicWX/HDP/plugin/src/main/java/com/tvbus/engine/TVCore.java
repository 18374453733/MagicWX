package com.tvbus.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;

public class TVCore {

    private Context context;
    private boolean onInited = false;
    private static TVCore inst = null;
    private static String mUri = null;
    private static boolean isSo = false;
    private static String httpUri = null;
    private static long nativeHandle = -1L;
    private static boolean loadError;

    private TVListener tvListener = new TVListener() {
        @Override
        public void onInited(String result) {
            Log.d("TVCore-onInited", result);
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject obj = new JSONObject(result.trim());
                    if (obj.has("tvcore")) {
                        onInited = "0".equals(obj.optString("tvcore"));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStart(String result) {
            Log.d("TVCore-onStart", result);
        }

        @Override
        public void onPrepared(String result) {
            Log.d("TVCore-onPrepared", result);
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject obj = new JSONObject(result.trim());
                    if (obj.has("http")) {
                        String http = obj.optString("http");
                        if (!TextUtils.isEmpty(http)) {
                            httpUri = http;
                        }
                    } else if (obj.has("hls")) {
                        String hls = obj.optString("hls");
                        if (!TextUtils.isEmpty(hls)) {
                            mUri = hls;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onInfo(String result) {
            Log.d("TVCore-onInfo", result);
        }

        @Override
        public void onStop(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject obj = new JSONObject(result.trim());
                    if (obj.has("errno") && !"0".equals(obj.optString("errno"))) {
                        loadError = true;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            Log.d("TVCore-onStop", result);
        }

        @Override
        public void onQuit(String result) {
            Log.d("TVCore-onQuit", result);
        }
    };

    private TVCore(Context context) {
        this.context = context.getApplicationContext();
        try {
            synchronized (this) {
                if (!isSo) {
                    isSo = true;
                    final File libraryFile = new File(context.getDir("libs", Context.MODE_PRIVATE), "libtvcore.so");
                    if (libraryFile.exists()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                System.load(libraryFile.getAbsolutePath());
                                nativeHandle = initialise();
                                if (nativeHandle != -1) {
                                    setListener(nativeHandle, tvListener);
                                    Thread thread = new Thread(new TVServer());
                                    thread.setName("tvcore");
                                    thread.start();
                                }
                            }
                        });
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static TVCore get(Context context) {
        if (inst == null) {
            inst = new TVCore(context);
        }
        return inst;
    }

    private void isonInited(){
        synchronized (this) {
            if (!onInited) {
                try {
                    int retryCount = 200;
                    while (!onInited && (retryCount--) > 0) {
                        Thread.sleep(100L);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String playUrl(String uri) {
        mUri = null;
        httpUri = null;
        isonInited();
        if (onInited) {
            try {
                start(uri);
                int retryCount = 300;
                while (mUri == null && (retryCount--) > 0 && !loadError) {
                    Thread.sleep(100L);
                }
                loadError = false;
                if (mUri == null && httpUri != null) {
                    mUri = httpUri;
                }
                if (mUri == null) {
                    stop(nativeHandle);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return mUri;
    }

    public static boolean close(Context context) {
        if (isSo) {
            TVCore.get(context).quit();
        }
        return true;
    }

    private void setPlayPort(int iPort) {
        if (nativeHandle != -1) {
            try {
                setPlayPort(nativeHandle, iPort);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void setServPort(int iPort) {
        if (nativeHandle != -1) {
            try {
                setServPort(nativeHandle, iPort);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void start(String url) {
        if (nativeHandle != -1) {
            try {
                start(nativeHandle, url);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void start(String url, String accessCode) {
        if (nativeHandle != -1) {
            try {
                start2(nativeHandle, url, accessCode);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopPlay() {
        if (nativeHandle != -1 && mUri != null) {
            try {
                stop(nativeHandle);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            mUri = null;
        }
    }

    private int p2p_run() {
        if (nativeHandle != -1) {
            try {
                return run(nativeHandle);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private void quit() {
        if (nativeHandle != -1) {
            try {
                quit(nativeHandle);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private native int init(long handle, Context context);

    private native int run(long handle);

    private native void start(long handle, String url);

    private native void start2(long handle, String url, String accessCode);

    private native static void stop(long handle);

    private native void quit(long handle);

    private native void setServPort(long handle, int iPort);

    private native void setPlayPort(long handle, int iPort);

    private native void setListener(long handle, TVListener listener);

    private native long initialise();

    private class TVServer implements Runnable {

        @Override
        public void run() {
            setPlayPort(8602);
            setServPort(4610);
            if (init(nativeHandle, context) == 0) {
                p2p_run();
            }
        }
    }
}