package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.request.MemberLoginRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberRegisterRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.AuthService;
import com.wjd4782.taxiprojectrestapi.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/register")
    public ResponseDto<Boolean> register(@RequestBody MemberRegisterRequestDto requestDto) {
        ResponseDto<Boolean> responseDto = authService.register(requestDto);
        return responseDto;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseDto<TokenResponseDto> login(@RequestBody MemberLoginRequestDto requestDto) {
        ResponseDto<TokenResponseDto> responseDto = authService.login(requestDto);
        return responseDto;
    }

    // 아이디 중복 검사
    @GetMapping("/check-memberId/{memberId}")
    public ResponseDto<Boolean> duplicateCheckMemberId(@PathVariable("memberId") String memberId) {
        ResponseDto<Boolean> responseDto = authService.DuplicateCheckMemberId(memberId);
        return responseDto;
    }

    // 닉네임 중복 검사
    @GetMapping("/check-nickname/{nickname}")
    public ResponseDto<Boolean> duplicateCheckNickname(@PathVariable("nickname") String nickname) {
        ResponseDto<Boolean> responseDto = authService.DuplicateCheckNickname(nickname);
        return responseDto;
    }
}
