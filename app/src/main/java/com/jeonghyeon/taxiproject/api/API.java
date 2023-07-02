package com.jeonghyeon.taxiproject.api;

import com.jeonghyeon.taxiproject.dto.TaxiStandResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface API {
    @GET("/api/taxiStand")
    Call<List<TaxiStandResponse>> findAllTaxiStand();
}
