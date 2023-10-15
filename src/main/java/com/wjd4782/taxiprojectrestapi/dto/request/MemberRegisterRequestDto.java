package com.wjd4782.taxiprojectrestapi.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRegisterRequestDto {
    private String memberId;
    private String password;
    private String nickname;
    private int gender; // 남자 : 0, 여자 : 1
}
