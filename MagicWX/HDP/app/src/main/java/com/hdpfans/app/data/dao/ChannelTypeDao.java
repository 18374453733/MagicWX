package com.hdpfans.app.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.hdpfans.app.model.entity.ChannelTypeModel;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ChannelTypeDao {
    @Query("delete from channel_type")
    void nukeTable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateChannelType(ChannelTypeModel... channelTypeModels);

    @Query("SELECT * FROM channel_type WHERE hidden != 1 LIMIT 1")
    Single<ChannelTypeModel> queryFirstChannelType();

    @Query("SELECT * FROM channel_type WHERE hidden != 1 ORDER BY weigh")
    Single<List<ChannelTypeModel>> getAll();

    @Query("SELECT * FROM channel_type ORDER BY weigh")
    Single<List<ChannelTypeModel>> getAllIncludeHidden();

    @Delete
    void delete(ChannelTypeModel... channelTypeModels);

    @Query("DELETE FROM channel_type WHERE id = :id")
    void deleteById(int id);
}
