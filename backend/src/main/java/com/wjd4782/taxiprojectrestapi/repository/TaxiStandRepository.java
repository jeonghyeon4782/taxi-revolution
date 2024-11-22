package com.wjd4782.taxiprojectrestapi.repository;

import com.wjd4782.taxiprojectrestapi.domain.TaxiStand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxiStandRepository extends JpaRepository<TaxiStand, Long> {
}
