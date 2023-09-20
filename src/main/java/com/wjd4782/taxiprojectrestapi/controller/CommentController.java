package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.request.CommentAddRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.CommentUpdateRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.CommentResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    // 댓글 조회
    @GetMapping("/{postId}")
    public ResponseDto<List<CommentResponseDto>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        ResponseDto<List<CommentResponseDto>> responseDto = commentService.getCommentsByPostId(postId);
        return responseDto;
    }

    // 댓글 작성
    @PostMapping("/{postId}")
    public ResponseDto<Boolean> addComment(@PathVariable("postId") Long postId, @RequestBody CommentAddRequestDto requestDto, Authentication authentication) {
        ResponseDto<Boolean> responseDto = commentService.addComment(postId, requestDto ,authentication.getName());
        return responseDto;
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseDto<Boolean> deletePost(@PathVariable("commentId") Long commentId) {
        ResponseDto<Boolean> responseDto = commentService.deleteComment(commentId);
        return responseDto;
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseDto<Boolean> updatePost(@PathVariable("commentId") Long commentId, @RequestBody CommentUpdateRequestDto requestDto) {
        ResponseDto<Boolean> responseDto = commentService.updateComment(commentId, requestDto);
        return responseDto;
    }
}
