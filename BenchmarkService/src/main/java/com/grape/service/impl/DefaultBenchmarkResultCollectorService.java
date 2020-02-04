package com.grape.service.impl;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkResult;
import com.grape.service.BenchmarkResultCollectorService;
import com.grape.service.ConnectionSupervisor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class DefaultBenchmarkResultCollectorService implements BenchmarkResultCollectorService {

    private static final String ENDPOINT_FORMAT = "http://%s:%d%s";
    private final RestTemplate restTemplate;
    private final ConnectionSupervisor singleConnectionSupervisor;

    @Override
    public List<AggregatedBenchmarkResult> collectAggregatedBenchmarkResults(Long iterations, Benchmark benchmark) {
        if (singleConnectionSupervisor.approveBenchmarking(benchmark)) {
            List<AggregatedBenchmarkResult> aggregatedBenchmarkResults = benchmark.getBenchmarkEndpoints().stream()
                    .map(benchmarkEndpoint -> performIterations(iterations, benchmark, benchmarkEndpoint))
                    .collect(toList());
            singleConnectionSupervisor.endBenchmarking(benchmark);
            return aggregatedBenchmarkResults;
        }
        return Collections.emptyList();
    }

    @Override
    public List<BenchmarkResult> collectBenchmarkResults(Benchmark benchmark) {
        if (singleConnectionSupervisor.approveBenchmarking(benchmark)) {
            List<BenchmarkResult> benchmarkResults = benchmark.getBenchmarkEndpoints().stream()
                    .map(benchmarkEndpoint -> performIteration(benchmark, benchmarkEndpoint))
                    .collect(toList());
            singleConnectionSupervisor.endBenchmarking(benchmark);
            return benchmarkResults;
        }
        return Collections.emptyList();
    }

    private BenchmarkResult performIteration(Benchmark benchmark, String benchmarkEndpoint) {
        return callForEntityBody(getFormattedUrl(benchmark, benchmarkEndpoint));
    }

    private AggregatedBenchmarkResult performIterations(Long iterations, Benchmark benchmark, String benchmarkEndpoint) {
        AggregatedBenchmarkResult aBenchmarkResult = AggregatedBenchmarkResult.builder()
                .iterations(iterations)
                .build();
        for (int i = 0; i < iterations; ++i) {
            BenchmarkResult benchmarkResult = performIteration(benchmark, benchmarkEndpoint);
            aBenchmarkResult.setTotalTime(aBenchmarkResult.getTotalTime() + benchmarkResult.getIndicators().getTimeInSec());
            aBenchmarkResult.setLastBenchmarkResult(benchmarkResult);
        }
        aBenchmarkResult.setAverageTime(aBenchmarkResult.getTotalTime() / aBenchmarkResult.getIterations());
        return aBenchmarkResult;
    }

    private BenchmarkResult callForEntityBody(String url) {
        return restTemplate.getForEntity(url, BenchmarkResult.class).getBody();
    }

    private String getFormattedUrl(Benchmark benchmark, String endpoint) {
        return String.format(ENDPOINT_FORMAT, benchmark.getHostName(), benchmark.getPort(), endpoint);
    }
}
