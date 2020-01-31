package com.grape.facade.impl;

import com.grape.domain.AggregatedBenchmarkResult;
import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkResult;
import com.grape.facade.BenchmarkFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DefaultBenchmarkFacade implements BenchmarkFacade {

    private static final String ENDPOINT_FORMAT = "http://%s:%d%s";
    private final RestTemplate restTemplate;

    @Override
    public Stream<AggregatedBenchmarkResult> collectAggregatedBenchmarkResults(Long iterations, Benchmark benchmark) {
        return benchmark.getBenchmarkEndpoints().stream()
                .map(benchmarkEndpoint -> performIterations(iterations, benchmark, benchmarkEndpoint));
    }

    private AggregatedBenchmarkResult performIterations(Long iterations, Benchmark benchmark, String benchmarkEndpoint) {
        AggregatedBenchmarkResult aBenchmarkResult = AggregatedBenchmarkResult.builder()
                .iterations(iterations)
                .build();
        for (int i = 0; i < iterations; ++i) {
            BenchmarkResult benchmarkResult = callForEntityBody(getFormattedUrl(benchmark, benchmarkEndpoint));
            aBenchmarkResult.setTotalTime(aBenchmarkResult.getTotalTime() + benchmarkResult.getIndicators().getTimeInSec());
            aBenchmarkResult.setLastBenchmarkResult(benchmarkResult);
        }
        aBenchmarkResult.setAverageTime(aBenchmarkResult.getTotalTime() / aBenchmarkResult.getIterations());
        return aBenchmarkResult;
    }

    @Override
    public BenchmarkResult callForEntityBody(String url) {
        return restTemplate.getForEntity(url, BenchmarkResult.class).getBody();
    }

    @Override
    public String getFormattedUrl(Benchmark benchmark, String endpoint) {
        return String.format(ENDPOINT_FORMAT, benchmark.getHostName(), benchmark.getPort(), endpoint);
    }
}
