package com.jeonghyeon.taxiproject.domain;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Record")
public class Record implements Serializable {

    // 아이디
    @PrimaryKey(autoGenerate = true) // autoIncrement 설정
    private long recordId;

    private long boardingTime; // 승차시간
    private long alightingTime; // 하차시간
    private String vehicleNumber; // 차량번호
    private String boardingLocation; // 승차위치
    private String alightingLocation; // 하차위치

    public Record() {}

    public Record(long boardingTime, long alightingTime, String vehicleNumber, String boardingLocation, String alightingLocation) {
        this.boardingTime = boardingTime;
        this.alightingTime = alightingTime;
        this.vehicleNumber = vehicleNumber;
        this.boardingLocation = boardingLocation;
        this.alightingLocation = alightingLocation;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getBoardingTime() {
        return boardingTime;
    }

    public void setBoardingTime(long boardingTime) {
        this.boardingTime = boardingTime;
    }

    public long getAlightingTime() {
        return alightingTime;
    }

    public void setAlightingTime(long alightingTime) {
        this.alightingTime = alightingTime;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getBoardingLocation() {
        return boardingLocation;
    }

    public void setBoardingLocation(String boardingLocation) {
        this.boardingLocation = boardingLocation;
    }

    public String getAlightingLocation() {
        return alightingLocation;
    }

    public void setAlightingLocation(String alightingLocation) {
        this.alightingLocation = alightingLocation;
    }
}