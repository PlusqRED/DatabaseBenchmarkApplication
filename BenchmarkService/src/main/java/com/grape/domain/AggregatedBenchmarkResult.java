package com.grape.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AggregatedBenchmarkResult {
    private BenchmarkResult lastBenchmarkResult;
    private double totalTime;
    private double averageTime;
    private long iterations;
}
