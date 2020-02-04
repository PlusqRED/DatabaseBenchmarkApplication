package com.grape.service;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;

import java.util.List;

public interface BenchmarkResultCollectorService {
    List<AggregatedBenchmarkResult> collectAggregatedBenchmarkResults(Long iterations, Benchmark benchmark);
}
