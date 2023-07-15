package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.request.MemberLoginRequest;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberRegisterRequest;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.AuthService;
import com.wjd4782.taxiprojectrestapi.dto.info.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService signService;

    // 회원가입
    @PostMapping("/register")
    public ResponseDto<String> register(@RequestBody MemberRegisterRequest requestDto) {
        ResponseDto<String> responseDto = signService.register(requestDto);
        return responseDto;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseDto<TokenResponse> login(@RequestBody MemberLoginRequest requestDto) {
        ResponseDto<TokenResponse> responseDto = signService.login(requestDto);
        return responseDto;
    }
}
