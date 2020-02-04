package com.grape.controller.rest.impl;

import com.grape.controller.rest.BenchmarkController;
import com.grape.domain.Benchmark;
import com.grape.facade.BenchmarkFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/benchmarks")
public class RestBenchmarkController implements BenchmarkController {

    private final BenchmarkFacade benchmarkFacade;

    @Override
    public ResponseEntity<Map<String, ?>> benchmarkAll(@PathParam(value = "iterations") Long iterations) {
        if (iterations == null) {
            return ResponseEntity.ok(benchmarkFacade.benchmarkAll());
        }
        return ResponseEntity.ok(benchmarkFacade.benchmarkAllWithIterations(iterations));
    }

    @Override
    public ResponseEntity<Map<String, ?>> benchmark(
            @PathVariable("name") String benchmarkHostName,
            @PathParam(value = "iterations") Long iterations
    ) {
        Benchmark searchedBenchmark = benchmarkFacade.findBenchmark(benchmarkHostName);
        if (iterations == null) {
            return ResponseEntity.ok(benchmarkFacade.benchmark(searchedBenchmark));
        }
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
