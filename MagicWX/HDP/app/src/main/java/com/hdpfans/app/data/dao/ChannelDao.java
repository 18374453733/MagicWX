package com.hdpfans.app.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.hdpfans.app.model.entity.ChannelModel;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ChannelDao {

    @Query("SELECT * FROM channel")
    Single<List<ChannelModel>> queryAll();

    @Query("SELECT * FROM channel WHERE num = :num LIMIT 1")
    Single<ChannelModel> queryChannelByNum(long num);

    @Query("SELECT * FROM channel WHERE num = :num AND hidden != 1 LIMIT 1")
    ChannelModel queryChannelByNumNoHidden(long num);

    @Query("SELECT * FROM channel WHERE itemId = :tid AND hidden != 1 ORDER BY weigh ASC LIMIT 1")
    Single<ChannelModel> queryFirstChannel(long tid);

    @Query("SELECT * FROM channel WHERE hidden != 1 ORDER BY weigh ASC LIMIT 1")
    Single<ChannelModel> queryFirstChannel();

    @Query("SELECT * FROM channel WHERE itemId = :id AND hidden != 1 ORDER BY weigh ASC")
    Single<List<ChannelModel>> getChannelsByType(long id);

    @Query("SELECT * FROM channel WHERE itemId = :id ORDER BY weigh ASC")
    Single<List<ChannelModel>> getChannelsByTypeIncludeHidden(long id);

    @Query("DELETE FROM channel WHERE id in (:ids) AND itemId = :itemId")
    void deleteByIds(List<Integer> ids, int itemId);

    @Query("SELECT * FROM channel WHERE num in (:nums)")
    Single<List<ChannelModel>> queryByNums(List<Integer> nums);

    @Query("SELECT * FROM channel WHERE collect = 1 AND hidden != 1")
    Single<List<ChannelModel>> getCollectedChannel();

    @Query("SELECT COUNT(*) FROM channel WHERE collect = 1 AND hidden != 1")
    Single<Integer> countCollectedChannel();

    @Query("SELECT * FROM channel WHERE num LIKE :num || '%' ORDER BY num ASC")
    Single<List<ChannelModel>> searchChannelByNum(String num);

    @Query("SELECT * FROM channel WHERE name = :name ORDER BY num ASC LIMIT 1")
    Single<ChannelModel> searchChannelByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateChannel(ChannelModel... channelModels);

    @Query("UPDATE channel SET hidden = :hidden WHERE itemId = :itemId")
    void hiddenOrShowChannelByTypeId(int itemId, boolean hidden);


    @Query("DELETE FROM channel")
    void nukeTable();

    @Delete
    void delete(ChannelModel... channelModels);
}
