package com.grape.controller.rest.impl;

import com.grape.controller.rest.BenchmarkController;
import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.facade.BenchmarkFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/benchmarks")
public class RestBenchmarkController implements BenchmarkController {

    private final BenchmarkFacade benchmarkFacade;

    @Value("${iterations.error.message.negative}")
    private String iterationsErrorMessageNegative;

    @Override
    public ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmarkAll(
            @RequestParam(value = ITERATIONS, defaultValue = "1") Long iterations
    ) {
        validateIterations(iterations);
        return ResponseEntity.ok(benchmarkFacade.benchmarkAllWithIterations(iterations));
    }

    @Override
    public ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmark(
            @PathVariable(NAME) String benchmarkHostName,
            @RequestParam(value = ITERATIONS, defaultValue = "1") Long iterations
    ) {
        validateIterations(iterations);
        Benchmark searchedBenchmark = benchmarkFacade.findBenchmark(benchmarkHostName);
        return ResponseEntity.ok(benchmarkFacade.benchmarkWithIterations(searchedBenchmark, iterations));
    }

    @Override
    public ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark) {
        benchmarkFacade.registerBenchmark(benchmark);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Successfully registered!");
    }

    private void validateIterations(@RequestParam(value = ITERATIONS, defaultValue = "1") Long iterations) {
        if (iterations < 1) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, iterationsErrorMessageNegative);
        }
    }
}
