package com.grape.controller.rest;

import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkPool;
import com.grape.domain.BenchmarkResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class BenchmarkServiceController {

    private final RestTemplate restTemplate;
    private final BenchmarkPool benchmarkPool;

    private static final String ENDPOINT_FORMAT = "http://%s:%d%s";

    @GetMapping("/benchmark")
    public ResponseEntity<BenchmarkResult> benchmark() {
        List<String> benchmarksEndpoints = benchmarkPool.getBenchmarkList().entrySet().stream()
                .flatMap(benchmark -> benchmark.getValue().getBenchmarkEndpoints().stream()
                        .map(endpoint -> getFormattedEndpoint(benchmark.getValue(), endpoint)))
                .collect(toList());
        return ResponseEntity.ok(
                BenchmarkResult.builder()
                        .benchmarks(benchmarksEndpoints.parallelStream().map(this::callForEntityBody).collect(toList()))
                        .build()
        );
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark) {
        String hostNameAndPort = benchmark.getHostName()
                .concat(benchmark.getPort().toString());
        benchmarkPool.getBenchmarkList().put(hostNameAndPort, benchmark);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Successfully registered!");
    }

    private String callForEntityBody(String endpoint) {
        return restTemplate.getForEntity(endpoint, String.class).getBody();
    }

    private String getFormattedEndpoint(Benchmark benchmark, String endpoint) {
        return String.format(ENDPOINT_FORMAT, benchmark.getHostName(), benchmark.getPort(), endpoint);
    }
}
