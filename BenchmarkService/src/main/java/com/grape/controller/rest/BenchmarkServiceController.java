package com.grape.controller.rest;

import com.grape.domain.AggregatedBenchmarkResult;
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

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class BenchmarkServiceController {

    private static final String ENDPOINT_FORMAT = "http://%s:%d%s";
    private final RestTemplate restTemplate;
    private final BenchmarkPool benchmarkPool;

    @GetMapping("/benchmark")
    public ResponseEntity<List<?>> benchmark(@PathParam(value = "iterations") Long iterations) {
        List<String> benchmarksEndpoints = benchmarkPool.getBenchmarkList().entrySet().stream()
                .flatMap(benchmark -> benchmark.getValue().getBenchmarkEndpoints().stream()
                        .map(endpoint -> getFormattedUrl(benchmark.getValue(), endpoint)))
                .collect(toList());

        if (iterations == null) {
            List<BenchmarkResult> benchmarkResults = benchmarksEndpoints.parallelStream()
                    .map(this::callForEntityBody)
                    .collect(toList());
            return ResponseEntity.ok(benchmarkResults);
        }

        List<AggregatedBenchmarkResult> aggregatedBenchmarkResults =
                benchmarkPool.getBenchmarkList().values().parallelStream()
                        .flatMap(benchmark -> collectAggregatedBenchmarkResults(iterations, benchmark))
                        .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(aggregatedBenchmarkResults);
    }

    private Stream<AggregatedBenchmarkResult> collectAggregatedBenchmarkResults(Long iterations, Benchmark benchmark) {
        return benchmark.getBenchmarkEndpoints().stream()
                .map(benchmarkEndpoint -> performIterations(iterations, benchmark, benchmarkEndpoint));
    }

    private AggregatedBenchmarkResult performIterations(Long iterations, Benchmark benchmark, String benchmarkEndpoint) {
        AggregatedBenchmarkResult aBenchmarkResult = AggregatedBenchmarkResult.builder()
                .iterations(iterations)
                .build();
        for (int i = 0; i < iterations; ++i) {
            BenchmarkResult benchmarkResult = callForEntityBody(getFormattedUrl(benchmark, benchmarkEndpoint));
            aBenchmarkResult.setTotalTime(aBenchmarkResult.getTotalTime() + benchmarkResult.getTimeInSec());
            aBenchmarkResult.setLastBenchmarkResult(benchmarkResult);
        }
        aBenchmarkResult.setAverageTime(aBenchmarkResult.getTotalTime() / aBenchmarkResult.getIterations());
        return aBenchmarkResult;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark) {
        String hostNameAndPort = benchmark.getHostName()
                .concat(benchmark.getPort().toString());
        benchmarkPool.getBenchmarkList().put(hostNameAndPort, benchmark);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Successfully registered!");
    }

    private BenchmarkResult callForEntityBody(String url) {
        return restTemplate.getForEntity(url, BenchmarkResult.class).getBody();
    }

    private String getFormattedUrl(Benchmark benchmark, String endpoint) {
        return String.format(ENDPOINT_FORMAT, benchmark.getHostName(), benchmark.getPort(), endpoint);
    }
}
