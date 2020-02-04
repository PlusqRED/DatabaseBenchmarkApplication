package com.grape.facade;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkResult;

import java.util.List;
import java.util.Map;

public interface BenchmarkFacade {
    Benchmark findBenchmark(String benchmarkHostName);

    Map<String, List<BenchmarkResult>> benchmarkAll();

    Map<String, List<AggregatedBenchmarkResult>> benchmarkAllWithIterations(Long iterations);

    Map<String, List<BenchmarkResult>> benchmark(Benchmark benchmark);

    Map<String, List<AggregatedBenchmarkResult>> benchmarkWithIterations(Benchmark benchmark, Long iterations);

    void registerBenchmark(Benchmark benchmark);
}
