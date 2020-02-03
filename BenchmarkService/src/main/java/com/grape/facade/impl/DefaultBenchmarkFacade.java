package com.grape.facade.impl;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkResult;
import com.grape.facade.BenchmarkFacade;
import com.grape.service.ConnectionSupervisor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DefaultBenchmarkFacade implements BenchmarkFacade {

    private static final String ENDPOINT_FORMAT = "http://%s:%d%s";
    private final RestTemplate restTemplate;
    private final ConnectionSupervisor singleConnectionSupervisor;

    @Override
    public Stream<AggregatedBenchmarkResult> collectAggregatedBenchmarkResults(Long iterations, Benchmark benchmark) {
        if(singleConnectionSupervisor.approveBenchmarking(benchmark)) {
            Stream<AggregatedBenchmarkResult> aggregatedBenchmarkResultStream = benchmark.getBenchmarkEndpoints().stream()
                    .map(benchmarkEndpoint -> performIterations(iterations, benchmark, benchmarkEndpoint));
            singleConnectionSupervisor.endBenchmarking(benchmark);
            return aggregatedBenchmarkResultStream;
        }
        return Stream.empty();
    }

    @Override
    public Stream<BenchmarkResult> collectBenchmarkResults(Benchmark benchmark) {
        if (singleConnectionSupervisor.approveBenchmarking(benchmark)) {
            Stream<BenchmarkResult> benchmarkResultStream = benchmark.getBenchmarkEndpoints().stream()
                    .map(benchmarkEndpoint -> performIteration(benchmark, benchmarkEndpoint));
            singleConnectionSupervisor.endBenchmarking(benchmark);
            return benchmarkResultStream;
        }
        return Stream.empty();
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
