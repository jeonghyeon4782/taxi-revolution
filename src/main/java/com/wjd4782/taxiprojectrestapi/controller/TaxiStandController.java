package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.response.TaxiStandResponseDto;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.service.TaxiStandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RestController
@RequestMapping("/api/taxiStand")
public class TaxiStandController {

    private final TaxiStandService taxiStandService;

    // 택시 승차장 모든 데이터 가져오기
    @GetMapping("")
    public ResponseDto<List<TaxiStandResponseDto>> getAllTaxiStand() {
        ResponseDto<List<TaxiStandResponseDto>> responseDto = taxiStandService.findAll();
        return responseDto;
    }
}
