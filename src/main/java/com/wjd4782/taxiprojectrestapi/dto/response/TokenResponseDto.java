package com.wjd4782.taxiprojectrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
