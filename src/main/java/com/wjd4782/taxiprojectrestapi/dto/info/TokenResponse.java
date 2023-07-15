package com.wjd4782.taxiprojectrestapi.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
