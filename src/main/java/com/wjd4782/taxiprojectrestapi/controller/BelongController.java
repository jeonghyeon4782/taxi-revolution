package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.domain.Belong;
import com.wjd4782.taxiprojectrestapi.dto.request.PostAddRequestDto;
import com.wjd4782.taxiprojectrestapi.dto.response.PostResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.BelongService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/belong")
public class BelongController {

    private final BelongService belongService;

    // 소속 추가
    @PostMapping("/{postId}")
    public ResponseDto<Boolean> addBelong(@PathVariable("postId") Long postId, Authentication authentication) {
        ResponseDto<Boolean> responseDto = belongService.addBelong(postId, authentication.getName());
        return responseDto;
    }

    // 소속 삭제
    @DeleteMapping("/{belongId}")
    public ResponseDto<Boolean> deletePost(@PathVariable("belongId") Long belongId) {
        ResponseDto<Boolean> responseDto = belongService.deleteBelong(belongId);
        return responseDto;
    }

    // 나의 모든 소속 조회
//    @GetMapping("/mine")
//    public ResponseDto<List<PostResponseDto>> getAllMyPost(Authentication authentication) {
//        ResponseDto<List<PostResponseDto>> responseDto = postService.getAllMyPost(authentication.getName());
//        return responseDto;
//    }
}
