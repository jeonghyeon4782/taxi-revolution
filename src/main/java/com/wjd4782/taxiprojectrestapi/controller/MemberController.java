package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.info.MemberResponse;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberUpdateRequest;
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
    public ResponseDto<MemberResponse> getMyInfo(Authentication authentication) {
        ResponseDto<MemberResponse> responseDto = memberService.getMemberInfo(authentication.getName());
        return responseDto;
    }

    // 특정 유저 찾기
    @GetMapping("{memberId}")
    public ResponseDto<MemberResponse> getMemberInfo(@PathVariable("memberId") String memberId) {
        ResponseDto<MemberResponse> responseDto = memberService.getMemberInfo(memberId);
        return responseDto;
    }

    // 내 정보 수정
    @PutMapping("")
    public ResponseDto<String> updateMyInfo(Authentication authentication, @RequestBody MemberUpdateRequest requestDto) {
        ResponseDto<String> responseDto = memberService.updateMemberInfo(authentication.getName(), requestDto);
        return responseDto;
    }
}
