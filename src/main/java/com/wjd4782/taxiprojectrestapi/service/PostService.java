package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Belong;
import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Post;
import com.wjd4782.taxiprojectrestapi.dto.request.PostAddRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.PostUpdateRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.PostResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.BelongRepository;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import com.wjd4782.taxiprojectrestapi.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BelongRepository belongRepository;

    // 글 작성
    @Transactional
    public ResponseDto<Boolean> addPost(PostAddRequestDto requestDto, Authentication authentication) {
        // 유저 찾기
        Member member = memberRepository.findByMemberId(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        // 출발 시간 String >> TimeStamp
        Timestamp departureTime = Timestamp.valueOf(requestDto.getDepartureTime());
        // 현재 시간 Date >> TimeStamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        Timestamp createTime = new Timestamp(currentTime.getTime());

        Post post = postRepository.save(
            Post.builder()
                    .title(requestDto.getTitle())
                    .content(requestDto.getContent())
                    .departureLocation(requestDto.getDepartureLocation())
                    .destinationLocation(requestDto.getDestinationLocation())
                    .recruitmentStatus(requestDto.getRecruitmentStatus())
                    .remainSeat(1)
                    .allSeat(requestDto.getAllSeat())
                    .departureTime(departureTime)
                    .createTime(createTime)
                    .member(member)
                    .build());

        Belong belong = belongRepository.save(
                Belong.builder()
                        .member(member)
                        .post(post)
                        .authority(0)
                        .build());

        return new ResponseDto<>(HttpStatus.OK.value(), "글 작성 성공", true);
    }

    // 모든 게시글 조회
    @Transactional
    public ResponseDto<List<PostResponseDto>> getAllPost() {
        List<PostResponseDto> posts = postRepository.findAll()
                .stream() // 리스트를 스트림으로 변환
                .map(PostResponseDto::new) // map() 메소드를 사용하여 각 taxiStand를 TaxiStandResponse 객체로 변환
                .toList(); // 스트림을 리스트로 변환
        ResponseDto<List<PostResponseDto>> responseDto = new ResponseDto<>(HttpStatus.OK.value(), "전체 게시글 조회 성공", posts);
        return responseDto;
    }

    // 내가 쓴 게시글 조회
    @Transactional
    public ResponseDto<List<PostResponseDto>> getAllMyPost(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        List<PostResponseDto> posts = postRepository.findByMember(member)
                .stream() // 리스트를 스트림으로 변환
                .map(PostResponseDto::new) // map() 메소드를 사용하여 각 taxiStand를 TaxiStandResponse 객체로 변환
                .toList(); // 스트림을 리스트로 변환
        ResponseDto<List<PostResponseDto>> responseDto = new ResponseDto<>(HttpStatus.OK.value(), "내가 쓴 게시글 조회 성공", posts);
        return responseDto;
    }

    // 특정 게시글 조회
    public ResponseDto<PostResponseDto> getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException("해당하는 게시물을 찾을 수 없습니다."));
        PostResponseDto responseDto = new PostResponseDto(post);
        return new ResponseDto<>(HttpStatus.OK.value(), "글 조회 성공", responseDto);
    }

    // 글 수정
    @Transactional
    public ResponseDto<Boolean> updatePost(PostUpdateRequestDto requestDto, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException("해당하는 게시물을 찾을 수 없습니다."));
        post.updatePost(requestDto);
        return new ResponseDto<>(HttpStatus.OK.value(), "게시글 수정 성공", true);
    }

    // 게시글 삭제
    @Transactional
    public ResponseDto<Boolean> deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException("해당하는 게시물을 찾을 수 없습니다."));
        postRepository.deleteById(postId);
        return new ResponseDto<>(HttpStatus.OK.value(), "게시글 삭제 성공", true);
    }
}
