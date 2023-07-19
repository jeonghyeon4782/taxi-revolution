package com.wjd4782.taxiprojectrestapi.dto.response;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MemberResponseDto {
    private String memberId;
    private String nickName;
    private int gender;

    // domain >> dto
    public MemberResponseDto(Member member) {
        this.memberId = member.getMemberId();
        this.nickName = member.getNickname();
        this.gender = member.getGender();
    }
}
