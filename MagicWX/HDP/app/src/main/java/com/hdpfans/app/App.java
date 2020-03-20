package com.hdpfans.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.baidu.mobstat.StatService;
import com.facebook.stetho.Stetho;
import com.hdpfans.app.internal.di.components.DaggerApplicationComponent;
import com.hdpfans.app.service.VoiceService;
import com.hdpfans.app.utils.BoxCompat;
import com.hdpfans.app.utils.PhoneCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import hdpfans.com.BuildConfig;

public class App extends DaggerApplication {

    private List<Activity> mActivitiesStack = new LinkedList<>();

    @Inject
    PluginLoader mPluginLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeLeakDetection();
        initializeStetho();
        initializeBugly();
        initializeMtj();

        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

        if (!BoxCompat.isPhoneRunning(this)) {
            startService(new Intent(this, VoiceService.class));
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mActivitiesStack.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivitiesStack.remove(activity);
        }
    };

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent.builder().create(this);
    }

    /**
     * Initialize leakcanary for check memory leak reference
     */
    private void initializeLeakDetection() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    private void initializeBugly() {
        String processName = PhoneCompat.getProcessName(android.os.Process.myPid());
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setUploadProcess(processName == null || processName.equals(getPackageName()));
        strategy.setAppChannel(BuildConfig.FLAVOR);
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy);
    }

    private void initializeStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    private void initializeMtj() {
        String processName = PhoneCompat.getProcessName(android.os.Process.myPid());
        if (processName == null || getPackageName().equals(processName)) {
            StatService.start(this);
            StatService.setDebugOn(BuildConfig.DEBUG);
            StatService.setForTv(this, true);
        }
    }

    public void exit() {
        for (Activity activity : mActivitiesStack) {
            activity.finish();
        }
    }

    public boolean isRunningActivity(Class<?> activityClass) {
        if (!mActivitiesStack.isEmpty()) {
            for (Activity activity : mActivitiesStack) {
                if (activity.getClass().getName().equals(activityClass.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public PluginLoader getPluginLoader() {
        return mPluginLoader;
    }
}
