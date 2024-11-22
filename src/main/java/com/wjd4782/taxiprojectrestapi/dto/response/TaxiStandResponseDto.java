package com.wjd4782.taxiprojectrestapi.dto.response;

import com.wjd4782.taxiprojectrestapi.domain.TaxiStand;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class TaxiStandResponseDto { // 택시승강장 정보를 응답받기 위한 DTO
    private double latitude; // 위도
    private double longitude; // 경도

    // domain >> dto
    @Builder
    public TaxiStandResponseDto(TaxiStand taxiStand) {
        this.latitude = taxiStand.getLatitude();
        this.longitude = taxiStand.getLongitude();
    }
}