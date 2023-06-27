package com.wjd4782.taxiprojectrestapi.dto;

import com.wjd4782.taxiprojectrestapi.model.TaxiStand;
import lombok.Getter;

@Getter
public class TaxiStandResponse {
    private double latitude; // 위도
    private double longitude; // 경도

    public TaxiStandResponse(TaxiStand taxiStand) {
        this.latitude = taxiStand.getLatitude();
        this.longitude = taxiStand.getLongitude();
    }
}
