package com.grape.facade.impl;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkPool;
import com.grape.facade.BenchmarkFacade;
import com.grape.service.BenchmarkResultCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class DefaultBenchmarkFacade implements BenchmarkFacade {

    private final BenchmarkPool benchmarkPool;
    private final BenchmarkResultCollectorService benchmarkResultCollectorService;

    @Value("${benchmark.error.message.notFound}")
    private String benchmarkErrorMessageNotFound;

    @Override
    public Benchmark findBenchmark(String benchmarkHostName) {
        return benchmarkPool.getBenchmarkList().values().stream()
                .filter(benchmark -> benchmark.getHostName().equalsIgnoreCase(benchmarkHostName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(benchmarkErrorMessageNotFound));
    }

    @Override
    public Map<String, List<AggregatedBenchmarkResult>> benchmarkAllWithIterations(Long iterations) {
        return benchmarkPool.getBenchmarkList().values().parallelStream()
                .map(benchmark -> benchmarkResultCollectorService.collectAggregatedBenchmarkResults(iterations, benchmark))
                .flatMap(List::stream)
                .collect(groupingBy(e -> e.getLastBenchmarkResult().getHostName()));
    }

    @Override
    public Map<String, List<AggregatedBenchmarkResult>> benchmarkWithIterations(Benchmark benchmark, Long iterations) {
        return benchmarkResultCollectorService.collectAggregatedBenchmarkResults(iterations, benchmark).stream()
                .collect(groupingBy(e -> e.getLastBenchmarkResult().getHostName()));
    }

    @Override
    public void registerBenchmark(Benchmark benchmark) {
        String hostNameAndPort = benchmark.getHostName().concat(benchmark.getPort().toString());
        benchmarkPool.getBenchmarkList().put(hostNameAndPort, benchmark);
    }
}
