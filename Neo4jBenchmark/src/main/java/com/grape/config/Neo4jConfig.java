package com.grape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Neo4jConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
