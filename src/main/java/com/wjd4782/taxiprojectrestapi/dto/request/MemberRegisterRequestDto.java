package com.wjd4782.taxiprojectrestapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRegisterRequestDto {
    private String username;
    private String password;
    private String nickname;
    private int gender; // 남자 : 0, 여자 : 1
}
