package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.Belong;
import com.wjd4782.taxiprojectrestapi.domain.Comment;
import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Post;
import com.wjd4782.taxiprojectrestapi.dto.request.CommentAddRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.PostAddRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.CommentResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.PostResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.CommentRepository;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 조회
    @Transactional(readOnly = true)
    public ResponseDto<List<CommentResponseDto>> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPost_PostId(postId);
        List<CommentResponseDto> commentResponseDtos = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .commentId(comment.getCommentId())
                        .nickname(comment.getMember().getNickname())
                        .createAt(comment.getCreateAt().toString())
                        .content(comment.getContent())
                        .build())
                .collect(Collectors.toList());
        ResponseDto<List<CommentResponseDto>> responseDto = new ResponseDto<>(HttpStatus.OK.value(), "댓글 조회 성공", commentResponseDtos);
        return responseDto;
    }

    // 댓글 작성
    @Transactional
    public ResponseDto<Boolean> addComment(Long postId, CommentAddRequestDto requestDto, String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당하는 게시물을 찾을 수 없습니다."));
        Comment comment = commentRepository.save(
                Comment.builder()
                        .member(member)
                        .post(post)
                        .content(requestDto.getContent())
                        .build());


        return new ResponseDto<>(HttpStatus.OK.value(), "댓글 작성 성공", true);
    }

    // 댓글 삭제
    @Transactional
    public ResponseDto<Boolean> deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException("해당하는 댓글을 찾을 수 없습니다."));
        commentRepository.deleteById(commentId);
        return new ResponseDto<>(HttpStatus.OK.value(), "댓글 삭제 성공", true);
    }
}
