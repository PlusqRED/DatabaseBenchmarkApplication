package com.grape.controller.rest;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

@RequestMapping("/default")
public interface BenchmarkController {

    @GetMapping
    ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmarkAll(@PathParam(value = "iterations") Long iterations);

    @GetMapping("/{name}")
    ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmark(
            @PathVariable("name") String benchmarkHostName,
            @PathParam(value = "iterations") Long iterations
    );

    @PostMapping("/register")
    ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark);
}
