package com.hdpfans.app.internal.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Module(includes = {AndroidSupportInjectionModule.class, AndroidInjectionModule.class})
public class ApplicationModule {
    @Singleton
    @Provides
    Context provideApplicationContext(Application application) {
        return application;
    }
}
