package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.response.BelongPostResponseDto;
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
    public ResponseDto<Boolean> deleteBelong(@PathVariable("belongId") Long belongId) {
        ResponseDto<Boolean> responseDto = belongService.deleteBelong(belongId);
        return responseDto;
    }

    // 나의 소속 조회
    @GetMapping("/mine")
    public ResponseDto<List<BelongPostResponseDto>> getBelongPost(Authentication authentication) {
        ResponseDto<List<BelongPostResponseDto>> responseDto = belongService.getBelongPost(authentication.getName());
        return responseDto;
    }
}
