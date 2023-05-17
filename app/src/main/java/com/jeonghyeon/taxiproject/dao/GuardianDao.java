package com.jeonghyeon.taxiproject.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jeonghyeon.taxiproject.model.Guardian;

import java.util.List;

@Dao
public interface GuardianDao
{
    @Insert(onConflict = REPLACE) // 덮어 쓰기
    void insert(Guardian guardian);

    @Delete
    void delete(Guardian guardian);

    @Delete
    void reset(List<Guardian> mainData);

    @Query("UPDATE Guardian SET guardianNum = :guardianNum WHERE guardianId = :guardianId")
    void update(int guardianId, String guardianNum);

    @Query("SELECT * FROM Guardian")
    List<Guardian> getAll();
}
