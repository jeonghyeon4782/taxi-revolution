package com.jeonghyeon.taxiproject.api;

import com.jeonghyeon.taxiproject.dto.request.LoginRequest;
import com.jeonghyeon.taxiproject.dto.request.ResisterRequest;
import com.jeonghyeon.taxiproject.dto.response.MemberResponse;
import com.jeonghyeon.taxiproject.dto.response.TaxiStandResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {
    @GET("/api/taxiStand")
    Call<List<TaxiStandResponse>> findAllTaxiStand();

    @POST("/auth/login")
    Call<MemberResponse> login(@Body LoginRequest loginRequest);

    @POST("/auth/register")
    Call<MemberResponse> resister(@Body ResisterRequest resisterRequest);
}
