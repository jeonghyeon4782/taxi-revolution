package com.jeonghyeon.taxiproject.roomDB.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jeonghyeon.taxiproject.domain.Record;

import java.util.List;

@Dao
public interface RecordDao {
    @Insert(onConflict = REPLACE) // 덮어 쓰기
    void insert(Record record);

    @Delete
    void delete(Record record);

    @Update
    void update(Record record);

    @Query("SELECT * FROM Record")
    List<Record> getAll();
}
