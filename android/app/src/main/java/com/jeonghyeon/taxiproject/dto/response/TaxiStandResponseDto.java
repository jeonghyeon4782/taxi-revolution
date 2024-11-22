package com.jeonghyeon.taxiproject.dto.response;

public class TaxiStandResponseDto {
    private double latitude; // 위도
    private double longitude; // 경도

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
