package com.grape.controller.rest.impl;

import com.grape.controller.rest.BenchmarkController;
import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.facade.BenchmarkFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/benchmarks")
public class RestBenchmarkController implements BenchmarkController {

    private final BenchmarkFacade benchmarkFacade;

    @Override
    public ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmarkAll(
            @RequestParam(value = "iterations", defaultValue = "1") Long iterations
    ) {
        return ResponseEntity.ok(benchmarkFacade.benchmarkAllWithIterations(iterations));
    }

    @Override
    public ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmark(
            @PathVariable("name") String benchmarkHostName,
            @RequestParam(value = "iterations", defaultValue = "1") Long iterations
    ) {
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
}
