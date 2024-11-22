package com.jeonghyeon.taxiproject.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Frequency")
public class Frequency implements Serializable {

    // Id
    @PrimaryKey(autoGenerate = true) // autoIncrement 설정
    private int frequencyId;

    // 문자 주기
    @ColumnInfo(name = "frequencyNum")
    private String frequencyNum;

    public int getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(int frequencyId) {
        this.frequencyId = frequencyId;
    }

    public String getFrequencyNum() {
        return frequencyNum;
    }

    public void setFrequencyNum(String frequencyNum) {
        this.frequencyNum = frequencyNum;
    }
}
