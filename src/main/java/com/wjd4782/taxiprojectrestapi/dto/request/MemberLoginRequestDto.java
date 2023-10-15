package com.wjd4782.taxiprojectrestapi.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberLoginRequestDto {
    private String memberId;
    private String password;
}
