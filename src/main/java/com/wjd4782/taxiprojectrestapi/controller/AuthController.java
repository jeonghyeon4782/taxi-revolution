package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.request.MemberLoginRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberRegisterRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.MemberResponseDto;
import com.wjd4782.taxiprojectrestapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService signService;

    // 회원가입
    @PostMapping("/register")
    public MemberResponseDto<Long> register(@RequestBody MemberRegisterRequestDto requestDto) {
        MemberResponseDto<Long> responseDto = signService.register(requestDto);
        return responseDto;
    }

    // 로그인
    @PostMapping("/login")
    public MemberResponseDto<String> login(@RequestBody MemberLoginRequestDto requestDto) {
        MemberResponseDto<String> responseDto = signService.login(requestDto);
        return responseDto;
    }
}
