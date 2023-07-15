package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.dto.info.TaxiStandResponse;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import com.wjd4782.taxiprojectrestapi.repository.TaxiStandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaxiStandService {

    private final TaxiStandRepository taxiStandRepository;

    @Transactional
    public ResponseDto<List<TaxiStandResponse>> findAll() {
        List<TaxiStandResponse> taxiStands = taxiStandRepository.findAll()
                .stream() // 리스트를 스트림으로 변환
                .map(TaxiStandResponse::new) // map() 메소드를 사용하여 각 taxiStand를 TaxiStandResponse 객체로 변환
                .toList(); // 스트림을 리스트로 변환

        ResponseDto<List<TaxiStandResponse>> responseDto = new ResponseDto<>(HttpStatus.OK.value(), "택시승강장 조회 성공", taxiStands);

        System.out.println(responseDto);

        return responseDto;
    }
}

