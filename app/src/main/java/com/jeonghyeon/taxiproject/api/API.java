package com.jeonghyeon.taxiproject.api;

import com.jeonghyeon.taxiproject.dto.request.PostAddRequestDto;
import com.jeonghyeon.taxiproject.dto.request.PostUpdateRequestDto;
import com.jeonghyeon.taxiproject.dto.response.BelongPostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.MemberResponseDto;
import com.jeonghyeon.taxiproject.dto.request.LoginRequestDto;
import com.jeonghyeon.taxiproject.dto.request.RegisterRequestDto;
import com.jeonghyeon.taxiproject.dto.response.PostResponseDto;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.dto.response.TaxiStandResponseDto;
import com.jeonghyeon.taxiproject.dto.response.TokenResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface API {

    @GET("/api/member")
    Call<ResponseDto<MemberResponseDto>> getMyInfo(@Header("Authorization") String authorization);

    @GET("/api/taxiStand")
    Call<ResponseDto<List<TaxiStandResponseDto>>> findAllTaxiStand();

    @POST("/api/auth/login")
    Call<ResponseDto<TokenResponseDto>> login(@Body LoginRequestDto loginRequestDto);

    @POST("/api/auth/register")
    Call<ResponseDto<String>> register(@Body RegisterRequestDto registerRequestDto);

    @GET("/api/auth/check-memberId/{memberId}")
    Call<ResponseDto<Boolean>> duplicateCheckMemberId(@Path("memberId") String memberId);

    @GET("api/auth/check-nickname/{nickname}")
    Call<ResponseDto<Boolean>> duplicateCheckNickname(@Path("nickname") String nickname);

    @PUT("/api/member/update-password/{password}")
    Call<ResponseDto<Boolean>> updatePassword(@Header("Authorization") String authorization, @Path("password") String password);

    @PUT("/api/member/update-nickname/{nickname}")
    Call<ResponseDto<Boolean>> updateNickname(@Header("Authorization") String authorization, @Path("nickname") String nickname);

    @DELETE("/api/member")
    Call<ResponseDto<Boolean>> deleteMyInfo(@Header("Authorization") String authorization);

    @GET("/api/post")
    Call<ResponseDto<List<PostResponseDto>>> getAllPost();

    @POST("/api/post")
    Call<ResponseDto<Boolean>> addPost(@Header("Authorization") String authorization, @Body PostAddRequestDto requestDto);

    @GET("/api/post/mine")
    Call<ResponseDto<List<PostResponseDto>>> getAllMyPost(@Header("Authorization") String authorization);

    @PUT("/api/post/{postId}")
    Call<ResponseDto<Boolean>> updatePost(@Header("Authorization") String authorization, @Body PostUpdateRequestDto requestDto, @Path("postId") Long postId);

    @DELETE("/api/post/{postId}")
    Call<ResponseDto<Boolean>> deletePost(@Header("Authorization") String authorization, @Path("postId") Long postId);

    // 나의 소속 조회
    @GET("/api/belong/mine")
    Call<ResponseDto<List<BelongPostResponseDto>>> getBelongPost(@Header("Authorization") String authorization);

    @POST("/api/belong/{postId}")
    Call<ResponseDto<Boolean>> addBelong(@Header("Authorization") String authorization, @Path("postId") Long postId);

    @DELETE("/api/belong/{belongId}")
    Call<ResponseDto<Boolean>> deleteBelong(@Header("Authorization") String authorization, @Path("belongId") Long belongId);
}
