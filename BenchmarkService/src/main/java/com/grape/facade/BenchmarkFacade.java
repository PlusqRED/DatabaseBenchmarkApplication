package com.grape.facade;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;

import java.util.List;
import java.util.Map;

public interface BenchmarkFacade {
    Benchmark findBenchmark(String benchmarkHostName);

    Map<String, List<AggregatedBenchmarkResult>> benchmarkAllWithIterations(Long iterations);

    Map<String, List<AggregatedBenchmarkResult>> benchmarkWithIterations(Benchmark benchmark, Long iterations);

    void registerBenchmark(Benchmark benchmark);
}
