package com.wjd4782.taxiprojectrestapi.controller;

import com.wjd4782.taxiprojectrestapi.dto.TaxiStandResponse;
import com.wjd4782.taxiprojectrestapi.service.TaxiStandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TaxiStandController {

    private final TaxiStandService taxiStandService;

    @GetMapping("/api/taxiStand")
    public ResponseEntity<List<TaxiStandResponse>> findAllTaxiStand() {
        List<TaxiStandResponse> taxiStands = taxiStandService.findAll()
                .stream()
                .map(TaxiStandResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(taxiStands);
    }
}
