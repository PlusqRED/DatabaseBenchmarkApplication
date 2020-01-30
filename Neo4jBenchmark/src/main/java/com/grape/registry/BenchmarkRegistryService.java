package com.grape.registry;

import com.grape.domain.Benchmark;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BenchmarkRegistryService implements CommandLineRunner {

    private final RestTemplate restTemplate;

    @Value("${benchmarkService.registry.name}")
    private String benchmarkServiceRegistryName;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void run(String... args) throws Exception {
        Benchmark benchmark = Benchmark.builder()
                .name(applicationName)
                .benchmarkEndpoints(List.of("/likes", "/friends"))
                .build();
        restTemplate.postForEntity(
                String.format("http://%s/register", benchmarkServiceRegistryName),
                benchmark,
                String.class
        );
    }
}
