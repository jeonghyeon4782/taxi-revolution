package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Belong;
import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Post;
import com.wjd4782.taxiprojectrestapi.repository.BelongRepository;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import com.wjd4782.taxiprojectrestapi.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BelongService {

    private final BelongRepository belongRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 소속 추가
    @Transactional
    public ResponseDto<Boolean> addBelong(Long postId, String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        Belong belong = belongRepository.save(
                Belong.builder()
                        .member(member)
                        .post(post)
                        .authority(1)
                        .build());
        return new ResponseDto<>(HttpStatus.OK.value(), "입장 성공", true);
    }

    // 소속 삭제
    @Transactional
    public ResponseDto<Boolean> deleteBelong(Long belongId) {
        Belong belong = belongRepository.findById(belongId).orElseThrow(()
                -> new EntityNotFoundException("해당하는 소속을 찾을 수 없습니다."));
        belongRepository.deleteById(belongId);
        return new ResponseDto<>(HttpStatus.OK.value(), "소속 삭제 성공", true);
    }
}
