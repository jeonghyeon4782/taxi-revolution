package com.jeonghyeon.taxiproject.api;

import com.jeonghyeon.taxiproject.dto.info.MemberInfo;
import com.jeonghyeon.taxiproject.dto.request.LoginRequest;
import com.jeonghyeon.taxiproject.dto.request.RegisterRequest;
import com.jeonghyeon.taxiproject.dto.response.ResponseDto;
import com.jeonghyeon.taxiproject.dto.info.TaxiStandInfo;
import com.jeonghyeon.taxiproject.dto.info.TokenInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface API {
    @GET("/api/member")
    Call<ResponseDto<MemberInfo>> getMyInfo(@Header("Authorization") String authorization);

    @GET("/api/taxiStand")
    Call<ResponseDto<List<TaxiStandInfo>>> findAllTaxiStand();

    @POST("/api/auth/login")
    Call<ResponseDto<TokenInfo>> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/register")
    Call<ResponseDto<String>> register(@Body RegisterRequest registerRequest);
}
