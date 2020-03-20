package com.hdpfans.app.internal.di.modules.provider;

import android.app.Application;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hdpfans.app.data.dao.AppDatabase;
import com.hdplive.jni.UrlDecode;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hdpfans.com.BuildConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {DaoSessionModule.class, ServiceModule.class})
public class ConfigModule {

    private Application application;

    public ConfigModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return this.application;
    }

    @Singleton
    @Provides
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        return loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Singleton
    @Provides
    public Cache providerHttpCache() {
        int cacheSize = 10 * 1024 * 1024; // 10MB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(Cache cache, HttpLoggingInterceptor loggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.cache(cache);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor);
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        return builder.build();
    }

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Singleton
    @Provides
    public OSSClient provideOssClient() {
        try {
            return Executors.newSingleThreadExecutor().submit(() -> {
                String endpoint = "http://update.juyoufan.net";
                if (BuildConfig.DEBUG) {
                    OSSLog.enableLog();
                }
                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                        UrlDecode.decode(application, "NvAIhGURezYWyALT"),
                        UrlDecode.decode(application, "X7hjuWO6jVzx14PGREZgt4r0NTCeYX"));
                ClientConfiguration clientConfiguration = new ClientConfiguration();
                clientConfiguration.setConnectionTimeout(15 * 1000);
                clientConfiguration.setSocketTimeout(15 * 1000);
                return new OSSClient(application, endpoint, credentialProvider, clientConfiguration);
            }).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Singleton
    @Provides
    public AppDatabase provideAppDatabase() {
        return AppDatabase.create(application);
    }
}
