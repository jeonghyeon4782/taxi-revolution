package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Role;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberLoginRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberRegisterRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.MemberResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import com.wjd4782.taxiprojectrestapi.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public MemberResponseDto<Long> register(MemberRegisterRequestDto requestDto) {
        // 중복 검사
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            return new MemberResponseDto<Long>(HttpStatus.BAD_REQUEST.value(), 0L); // 상태코드 400 리턴
        }
        Member user = memberRepository.save(
                Member.builder()
                        .username(requestDto.getUsername())
                        .password(passwordEncoder.encode(requestDto.getPassword()))
                        .nickname(requestDto.getNickname())
                        .gender(requestDto.getGender())
                        .roles(Collections.singletonList(Role.ROLE_MEMBER))
                        .build());
        return new MemberResponseDto<Long>(HttpStatus.OK.value(), user.getId());
    }

    // 로그인
    @Transactional
    public MemberResponseDto<String> login(MemberLoginRequestDto requestDto) {
        Member member = null;
        try {
            // 만약 아이디를 찾지 못한다면?
            member = memberRepository.findByUsername(requestDto.getUsername()).get();
        } catch (Exception e) {
            return new MemberResponseDto<String>(HttpStatus.BAD_REQUEST.value(), "아이디가 없습니다");
        }
        // 만약 비밀번호가 다르다면?
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword()))
        {
            return new MemberResponseDto<String>(HttpStatus.BAD_REQUEST.value(), "비밀번호가 다릅니다");
        }
        return new MemberResponseDto<String>(HttpStatus.OK.value(), jwtTokenProvider.createToken(requestDto.getUsername()));
    }
}
