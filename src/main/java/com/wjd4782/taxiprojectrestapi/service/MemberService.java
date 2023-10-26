package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Belong;
import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Post;
import com.wjd4782.taxiprojectrestapi.dto.response.MemberResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 정보 찾기
    @Transactional
    public ResponseDto<MemberResponseDto> getMemberInfo(String memberId) {
        MemberResponseDto responseDto = new MemberResponseDto(memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")));
        return new ResponseDto<>(HttpStatus.OK.value(), "유저 찾기 성공", responseDto);
    }

    // 유저 회원 탈퇴
    @Transactional
    public ResponseDto<Boolean> deleteMember(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        List<Belong> belongs = member.getBelongs();
        for (Belong belong : belongs) {
            Post post = belong.getPost();
            post.minusSeat();
        }
        memberRepository.deleteByMemberId(memberId);
        return new ResponseDto<>(HttpStatus.OK.value(), "유저 삭제 성공", true);
    }

    // 비밀번호 수정
    @Transactional
    public ResponseDto<Boolean> updatePassword(String memberId, String password) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()
                -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        member.updatePassword(passwordEncoder.encode(password));
        return new ResponseDto<>(HttpStatus.OK.value(), "비밀번호 수정 성공", true);
    }

    // 닉네임 수정
    @Transactional
    public ResponseDto<Boolean> updateNickname(String memberId, String nickname) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(()
                -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new DuplicateKeyException("중복된 닉네임입니다.");
        }
        member.updateNickname(nickname);
        return new ResponseDto<>(HttpStatus.OK.value(), "닉네임 수정 성공", true);
    }
}
