package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Belong;
import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Post;
import com.wjd4782.taxiprojectrestapi.dto.response.BelongMemberResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.BelongPostResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.BelongRepository;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import com.wjd4782.taxiprojectrestapi.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당하는 게시물을 찾을 수 없습니다."));
        // 이미 해당 회원이 해당 게시물에 소속되어 있는지 확인
        Belong existingBelong = belongRepository.findByMemberAndPost(member, post);
        if (existingBelong != null) {
            // 이미 소속이 있는 경우에 대한 예외처리
            throw new EntityNotFoundException("이미 해당 게시물에 소속되어 있습니다.");
        }
        post.plusSeat();
        Belong belong = belongRepository.save(
                Belong.builder()
                        .member(member)
                        .post(post)
                        .authority(1)
                        .gender(member.getGender())
                        .build());
        return new ResponseDto<>(HttpStatus.OK.value(), "입장 성공", true);
    }

    // 소속 삭제
    @Transactional
    public ResponseDto<Boolean> deleteBelong(Long belongId) {
        Belong belong = belongRepository.findById(belongId).orElseThrow(()
                -> new EntityNotFoundException("해당하는 소속을 찾을 수 없습니다."));
        belongRepository.deleteById(belongId);
        Post post = postRepository.findById(belong.getPost().getPostId()).orElseThrow(() -> new EntityNotFoundException("해당하는 게시물을 찾을 수 없습니다."));
        post.minusSeat();
        return new ResponseDto<>(HttpStatus.OK.value(), "소속 삭제 성공", true);
    }

    // 나의 소속 조회
    @Transactional
    public ResponseDto<List<BelongPostResponseDto>> getBelongPost(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        List<BelongPostResponseDto> belongPostResponseDtos = member.getBelongs().stream()
                .map(belong -> {
                    int authority = belong.getAuthority();
                    Post post = belong.getPost();
                    List<Belong> membersInPost = post.getBelongs();
                    List<BelongMemberResponseDto> belongMembers = membersInPost.stream()
                            .map(belongMember -> new BelongMemberResponseDto(belongMember.getBelongId(), belongMember.getMember().getNickname(), belongMember.getGender(), belongMember.getAuthority()))
                            .collect(Collectors.toList());
                    return BelongPostResponseDto.builder()
                            .postId(post.getPostId())
                            .belongId(belong.getBelongId())
                            .title(post.getTitle())
                            .departureLocation(post.getDepartureLocation())
                            .destinationLocation(post.getDestinationLocation())
                            .authority(authority) // 권한 정보 설정
                            .recruitmentStatus(post.getRecruitmentStatus())
                            .remainSeat(post.getRemainSeat())
                            .allSeat(post.getAllSeat())
                            .belongMembers(belongMembers)
                            .build();
                })
                .collect(Collectors.toList());
        ResponseDto<List<BelongPostResponseDto>> responseDto = new ResponseDto<>(HttpStatus.OK.value(), "나의 소속 조회", belongPostResponseDtos);
        return responseDto;
    }
}
