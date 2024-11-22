package com.jeonghyeon.taxiproject.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Guardian")
public class Guardian implements Serializable // 직렬화
{
    // Id
    @PrimaryKey(autoGenerate = true) // autoIncrement 설정
    private int guardianId;

    // 보호자 번호
    @ColumnInfo(name = "guardianNum")
    private String guardianNum;

    public int getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(int guardianId) {
        this.guardianId = guardianId;
    }

    public String getGuardianNum() {
        return guardianNum;
    }

    public void setGuardianNum(String guardianNum) {
        this.guardianNum = guardianNum;
    }
}
