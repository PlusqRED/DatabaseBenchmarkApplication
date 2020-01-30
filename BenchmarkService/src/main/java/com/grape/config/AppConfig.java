package com.grape.config;

import com.grape.domain.BenchmarkPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BenchmarkPool benchmarkList() {
        return BenchmarkPool.builder()
                .benchmarkList(new HashMap<>())
                .build();
    }
}
