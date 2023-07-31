package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.request.PostAddRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.request.PostUpdateRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.PostResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    // 글 작성
    @PostMapping("")
    public ResponseDto<Boolean> addPost(@RequestBody PostAddRequestDto requestDto, Authentication authentication) {
        ResponseDto<Boolean> responseDto = postService.addPost(requestDto, authentication);
        return responseDto;
    }

    // 모든 게시글 조회
    @GetMapping("")
    public ResponseDto<List<PostResponseDto>> getAllPost() {
        ResponseDto<List<PostResponseDto>> responseDto = postService.getAllPost();
        return responseDto;
    }

    // 내가 쓴 모든 게시글 조회
    @GetMapping("/mine")
    public ResponseDto<List<PostResponseDto>> getAllMyPost(Authentication authentication) {
        ResponseDto<List<PostResponseDto>> responseDto = postService.getAllMyPost(authentication.getName());
        return responseDto;
    }

    // 특정 글 조회
    @GetMapping("/{postId}")
    public ResponseDto<PostResponseDto> getPost(@PathVariable("postId") Long postId) {
        ResponseDto<PostResponseDto> responseDto = postService.getPost(postId);
        return responseDto;
    }

    // 글 수정
    @PutMapping("/{postId}")
    public ResponseDto<Boolean> updatePost(@RequestBody PostUpdateRequestDto requestDto, @PathVariable("postId") Long postId) {
        ResponseDto<Boolean> responseDto = postService.updatePost(requestDto, postId);
        return responseDto;
    }

    // 글 삭제
    @DeleteMapping("/{postId}")
    public ResponseDto<Boolean> deleteMyInfo(@PathVariable("postId") Long postId) {
        ResponseDto<Boolean> responseDto = postService.deletePost(postId);
        return responseDto;
    }
}
