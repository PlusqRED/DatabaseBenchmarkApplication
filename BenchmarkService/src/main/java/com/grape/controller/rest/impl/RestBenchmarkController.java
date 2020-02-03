package com.grape.controller.rest.impl;

import com.grape.controller.rest.BenchmarkController;
import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkPool;
import com.grape.domain.BenchmarkResult;
import com.grape.facade.BenchmarkFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/benchmarks")
public class RestBenchmarkController implements BenchmarkController {

    private final BenchmarkPool benchmarkPool;
    private final BenchmarkFacade benchmarkFacade;

    @Override
    public ResponseEntity<Map<String, ?>> benchmarkAll(@PathParam(value = "iterations") Long iterations) {
        List<String> benchmarkUrls = benchmarkPool.getBenchmarkList().entrySet().stream()
                .flatMap(benchmark -> benchmark.getValue().getBenchmarkEndpoints().stream()
                        .map(endpoint -> benchmarkFacade.getFormattedUrl(benchmark.getValue(), endpoint)))
                .collect(toList());

        if (iterations == null) {
            Map<String, List<BenchmarkResult>> results = benchmarkUrls.parallelStream()
                    .map(benchmarkFacade::callForEntityBody)
                    .collect(groupingBy(BenchmarkResult::getHostName));
            return ResponseEntity.ok(results);
        }

        Map<String, List<AggregatedBenchmarkResult>> results = benchmarkPool.getBenchmarkList().values().parallelStream()
                .flatMap(benchmark -> benchmarkFacade.collectAggregatedBenchmarkResults(iterations, benchmark))
                .collect(groupingBy(e -> e.getLastBenchmarkResult().getHostName()));
        return ResponseEntity.ok(results);
    }

    @Override
    public ResponseEntity<Map<String, ?>> benchmark(
            @PathVariable("name") String benchmarkHostName,
            @PathParam(value = "iterations") Long iterations
    ) {
        Benchmark searchedBenchmark = benchmarkPool.getBenchmarkList().entrySet().stream()
                .filter(entry -> entry.getValue().getHostName().equalsIgnoreCase(benchmarkHostName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Service not found!"))
                .getValue();

        List<String> benchmarkUrls = searchedBenchmark.getBenchmarkEndpoints().stream()
                .map(endpoint -> benchmarkFacade.getFormattedUrl(searchedBenchmark, endpoint))
                .collect(toList());

        if (iterations == null) {
            Map<String, List<BenchmarkResult>> results = benchmarkUrls.stream()
                    .map(benchmarkFacade::callForEntityBody)
                    .collect(groupingBy(BenchmarkResult::getHostName));
            return ResponseEntity.ok(results);
        }

        return ResponseEntity.ok(
                benchmarkFacade.collectAggregatedBenchmarkResults(iterations, searchedBenchmark)
                        .collect(groupingBy(e -> e.getLastBenchmarkResult().getHostName()))
        );
    }

    @Override
    public ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark) {
        String hostNameAndPort = benchmark.getHostName()
                .concat(benchmark.getPort().toString());
        benchmarkPool.getBenchmarkList().put(hostNameAndPort, benchmark);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Successfully registered!");
    }
}
