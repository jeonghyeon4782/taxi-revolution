package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.dto.info.MemberResponse;
import com.wjd4782.taxiprojectrestapi.dto.request.MemberUpdateRequest;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 찾기
    @Transactional
    public ResponseDto<MemberResponse> getMemberInfo(String memberId) {
        MemberResponse responseDto = new MemberResponse(memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")));
        return new ResponseDto<>(HttpStatus.OK.value(), "유저 찾기 성공", responseDto);
    }

    // 회원 정보 수정
    @Transactional
    public ResponseDto<String> updateMemberInfo(String memberId, MemberUpdateRequest requestDto) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        member.update(passwordEncoder.encode(requestDto.getPassword()), requestDto.getNickname());
        return new ResponseDto<>(HttpStatus.OK.value(), "유저 수정 성공", memberId);
    }
}
