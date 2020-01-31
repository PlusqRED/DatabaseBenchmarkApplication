package com.grape.domain;

import lombok.*;

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
