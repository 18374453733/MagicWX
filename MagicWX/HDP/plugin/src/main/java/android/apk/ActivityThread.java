package android.apk;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;

public class ActivityThread {

    private volatile static boolean isLoaded;

    private static ActivityThread Object;

    public native String sign(String data);

    public native int loadckey(String path);

    public native int kooinit(String sofile);

    public native int getp2pport();

    public native int getp2pstate(String str);

    public static ActivityThread currentActivityThread(Context context) {
        if (Object == null) {
            synchronized (ActivityThread.class) {
                if (Object == null) {
                    Object = new ActivityThread(context);
                }
            }
        }
        return Object;
    }

    public String getToken(String str) {
        try {
            return sign(str);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "123";
    }

    private ActivityThread(final Context context) {
        loadLibrary(context);
    }

    public boolean isIsLoaded() {
        return isLoaded;
    }

    private void loadLibrary(final Context context) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (!isLoaded) {
                        File libraryFile = new File(context.getDir("libs", Context.MODE_PRIVATE), "liblgsg.so");
                        if (libraryFile.exists()) {
                            System.load(libraryFile.getAbsolutePath());
                            isLoaded = true;
                        }
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}