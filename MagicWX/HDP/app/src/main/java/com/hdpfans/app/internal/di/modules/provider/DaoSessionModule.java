package com.hdpfans.app.internal.di.modules.provider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.hdpfans.app.data.dao.AppDatabase;
import com.hdpfans.app.data.dao.ChannelDao;
import com.hdpfans.app.data.dao.ChannelTypeDao;

@Module
public class DaoSessionModule {

    @Singleton
    @Provides
    public ChannelTypeDao provideChannelTypeDao(AppDatabase appDatabase) {
        return appDatabase.channelTypeDao();
    }

    @Singleton
    @Provides
    public ChannelDao provideChannelDao(AppDatabase appDatabase) {
        return appDatabase.channelDao();
    }

}
