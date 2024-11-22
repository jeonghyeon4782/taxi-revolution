package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberLoginRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberRegisterRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import com.wjd4782.taxiprojectrestapi.token.JwtTokenProvider;
import com.wjd4782.taxiprojectrestapi.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 회원가입
    @Transactional
    public ResponseDto<Boolean> register(MemberRegisterRequestDto requestDto) {
        // DB 저장
        Member member = memberRepository.save(
                Member.builder()
                        .memberId(requestDto.getMemberId())
                        .password(passwordEncoder.encode(requestDto.getPassword()))
                        .nickname(requestDto.getNickname())
                        .gender(requestDto.getGender())
                        .roles(Collections.singletonList("USER"))
                        .build());
        return new ResponseDto<>(HttpStatus.OK.value(), "회원가입 성공", true);
    }

    // 로그인
    @Transactional
    public ResponseDto<TokenResponseDto> login(MemberLoginRequestDto requestDto) {
        // 회원 조회
        Member member = memberRepository.findByMemberId(requestDto.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        // Login ID/PW 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getMemberId(), member.getPassword());
        // 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 인증 정보를 기반으로 JWT 토큰 생성
        return new ResponseDto<>(HttpStatus.OK.value(), "토큰 발급 완료", jwtTokenProvider.generateToken(authentication));
    }

    // 아이디 중복 검사
    @Transactional
    public ResponseDto<Boolean> DuplicateCheckMemberId(String memberId) {
        if (memberRepository.findByMemberId(memberId).isPresent()) {
            throw new DuplicateKeyException("중복된 아이디입니다.");
        }
        return new ResponseDto<>(HttpStatus.OK.value(), "사용 가능한 아이디입니다", true);
    }

    // 닉네임 중복 검사
    @Transactional
    public ResponseDto<Boolean> DuplicateCheckNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new DuplicateKeyException("중복된 닉네임입니다.");
        }
        return new ResponseDto<>(HttpStatus.OK.value(), "사용 가능한 닉네임입니다", true);
    }
}
