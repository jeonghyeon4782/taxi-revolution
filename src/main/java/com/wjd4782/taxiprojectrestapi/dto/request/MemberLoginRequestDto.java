package com.wjd4782.taxiprojectrestapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberLoginRequestDto {
    private String memberId;
    private String password;
}
