package com.wjd4782.taxiprojectrestapi.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class TokenResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenResponseDto(String grantType, String accessToken, String refreshToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
