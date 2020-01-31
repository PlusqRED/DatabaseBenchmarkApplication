package com.grape.controller.rest;

import com.grape.domain.Benchmark;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Map;

@RequestMapping("/default")
public interface BenchmarkController {

    @GetMapping
    ResponseEntity<Map<String, ?>> benchmarkAll(@PathParam(value = "iterations") Long iterations);

    @GetMapping("/{name}")
    ResponseEntity<Map<String, ?>> benchmark(
            @PathVariable("name") String benchmarkHostName,
            @PathParam(value = "iterations") Long iterations
    );

    @PostMapping("/register")
    ResponseEntity<String> registerBenchmark(@RequestBody Benchmark benchmark);
}
