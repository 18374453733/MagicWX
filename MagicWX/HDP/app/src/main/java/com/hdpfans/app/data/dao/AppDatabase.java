package com.hdpfans.app.data.dao;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;

import hdpfans.com.BuildConfig;

@Database(
        entities = {
                ChannelModel.class,
                ChannelTypeModel.class,
        },
        version = BuildConfig.DATABASE_VERSION
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChannelDao channelDao();

    public abstract ChannelTypeDao channelTypeDao();

    public static AppDatabase create(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, BuildConfig.DATABASE_NAME)
                .addMigrations(MIGRATION_2_3)
                .build();
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE channel_type ADD COLUMN weigh INTEGER NOT NULL DEFAULT 0");
        }
    };

}
