package com.jeonghyeon.taxiproject.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jeonghyeon.taxiproject.model.Frequency;

@Dao
public interface FrequencyDao {

    // 상위 하나의 객체만을 가져온다
    @Query("SELECT * FROM frequency LIMIT 1")
    Frequency getFrequency();

    // 객체 삽입
    @Insert
    void insert(Frequency frequency);

    // 객체 업데이트
    @Update
    void update(Frequency frequency);
}