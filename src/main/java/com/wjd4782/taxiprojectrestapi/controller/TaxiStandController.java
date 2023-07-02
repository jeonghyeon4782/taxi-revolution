package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.TaxiStandResponseDto;
import com.wjd4782.taxiprojectrestapi.service.TaxiStandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RestController
public class TaxiStandController {

    private final TaxiStandService taxiStandService;

    // 택시 승차장 모든 데이터 가져오기
    @GetMapping("/api/taxiStand")
    public ResponseEntity<List<TaxiStandResponseDto>> getAllTaxiStand() {
        List<TaxiStandResponseDto> taxiStands = taxiStandService.getAllTaxiStand()
                .stream() // 리스트를 스트림으로 변환
                .map(TaxiStandResponseDto::new) // map() 메소드를 사용하여 각 taxiStand를 TaxiStandResponse 객체로 변환
                .toList(); // 스트림을 리스트로 변환

        return ResponseEntity.ok() //  HTTP 응답으로 클라이언트에게 반환됩니다. HTTP 상태 코드는 200 OK로 설정되며, 반환되는 데이터는 리스트 형태로 포함
                .body(taxiStands);
    }
}
