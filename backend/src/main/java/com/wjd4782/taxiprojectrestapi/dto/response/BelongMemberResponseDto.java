package com.wjd4782.taxiprojectrestapi.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BelongMemberResponseDto {
    private Long belongId;
    private String nickname;
    private int gender;
    private int authority;

    @Builder
    public BelongMemberResponseDto(Long belongId, String nickname, int gender, int authority) {
        this.belongId = belongId;
        this.nickname = nickname;
        this.gender = gender;
        this.authority = authority;
    }
}
