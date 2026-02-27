package com.example.farmingcreditbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.example.farmingcreditbackend.mapper")
@EnableCaching
public class FarmingCreditBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmingCreditBackendApplication.class, args);
    }
}
