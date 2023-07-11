package com.wjd4782.taxiprojectrestapi.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaxiStand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key

    @Column(name = "latitude", nullable = false)
    private double latitude; // 위도

    @Column(name = "longitude", nullable = false)
    private double longitude; // 경도

    @Builder
    public TaxiStand(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
