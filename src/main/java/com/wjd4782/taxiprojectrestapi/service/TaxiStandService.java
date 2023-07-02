package com.wjd4782.taxiprojectrestapi.service;

import com.wjd4782.taxiprojectrestapi.domain.TaxiStand;
import com.wjd4782.taxiprojectrestapi.repository.TaxiStandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaxiStandService {
    private final TaxiStandRepository taxiStandRepository;

    public List<TaxiStand> getAllTaxiStand() {
        return taxiStandRepository.findAll();
    }
}

