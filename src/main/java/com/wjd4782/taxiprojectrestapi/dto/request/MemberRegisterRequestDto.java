package com.wjd4782.taxiprojectrestapi.dto.request;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRegisterRequestDto {
    private String memberId;
    private String password;
    private String nickname;
    private int gender; // 남자 : 0, 여자 : 1

    // dto >> domain
    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .build();
    }
}
