package com.grape.controller.rest;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

@RequestMapping("/default")
public interface BenchmarkController {
    String ITERATIONS = "iterations";
    String NAME = "name";

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmarkAll(@PathParam(ITERATIONS) Long iterations);

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, List<AggregatedBenchmarkResult>>> benchmark(
            @PathVariable(NAME) String benchmarkHostName,
            @PathParam(ITERATIONS) Long iterations
    );

    @PostMapping("/register")
    ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark);
}
