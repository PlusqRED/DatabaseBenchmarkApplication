package com.grape.registry;

import com.grape.domain.benchmark.Benchmark;
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

    @Value("${benchmarkService.registry.port}")
    private Integer benchmarkServiceRegistryPort;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @Override
    public void run(String... args) {
        Benchmark benchmark = Benchmark.builder()
                .hostName(applicationName)
                .port(serverPort)
                .benchmarkEndpoints(List.of("/likes", "/friends"))
                .build();
        restTemplate.postForEntity(
                String.format("http://%s:%d/benchmarks/register", benchmarkServiceRegistryName, benchmarkServiceRegistryPort),
                benchmark,
                String.class
        );
    }
}
