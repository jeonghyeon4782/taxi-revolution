package com.wjd4782.taxiprojectrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 기능 활성화
public class TaxiProjectRestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaxiProjectRestApiApplication.class, args);
    }
}