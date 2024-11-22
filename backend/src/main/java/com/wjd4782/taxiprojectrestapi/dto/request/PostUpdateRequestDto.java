package com.wjd4782.taxiprojectrestapi.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class PostUpdateRequestDto {
    private String title;
    private String content;
    private String departureLocation;
    private String destinationLocation;
    private String recruitmentStatus;
    private int allSeat;
    private String departureTime;
}
