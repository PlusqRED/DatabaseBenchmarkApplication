package com.grape.facade;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkResult;

import java.util.stream.Stream;

public interface BenchmarkFacade {
    Stream<AggregatedBenchmarkResult> collectAggregatedBenchmarkResults(Long iterations, Benchmark benchmark);

    BenchmarkResult callForEntityBody(String url);

    String getFormattedUrl(Benchmark benchmark, String endpoint);
}
