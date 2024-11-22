package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.response.MemberResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    // 내 정보 찾기
    @GetMapping("")
    public ResponseDto<MemberResponseDto> getMyInfo(Authentication authentication) {
        ResponseDto<MemberResponseDto> responseDto = memberService.getMemberInfo(authentication.getName());
        return responseDto;
    }

    // 유저 정보 찾기
    @GetMapping("/{memberId}")
    public ResponseDto<MemberResponseDto> getMemberInfo(@PathVariable("memberId") String memberId) {
        ResponseDto<MemberResponseDto> responseDto = memberService.getMemberInfo(memberId);
        return responseDto;
    }

    // 비밀번호 수정
    @PutMapping("/update-password/{password}")
    public ResponseDto<Boolean> updateMyPassword(Authentication authentication, @PathVariable("password") String password) {
        ResponseDto<Boolean> responseDto = memberService.updatePassword(authentication.getName(), password);
        return responseDto;
    }

    // 닉네임 수정
    @PutMapping("/update-nickname/{nickname}")
    public ResponseDto<Boolean> updateMyNickname(Authentication authentication, @PathVariable("nickname") String nickname) {
        ResponseDto<Boolean> responseDto = memberService.updateNickname(authentication.getName(), nickname);
        return responseDto;
    }

    // 회원 탈퇴
    @DeleteMapping("")
    public ResponseDto<Boolean> deleteMyInfo(Authentication authentication) {
        ResponseDto<Boolean> responseDto = memberService.deleteMember(authentication.getName());
        return responseDto;
    }
}
