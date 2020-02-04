package com.grape.service.impl;

import com.grape.domain.*;
import com.grape.service.ConnectionSupervisor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DefaultBenchmarkResultCollectorService.class)
class DefaultBenchmarkResultCollectorServiceTest {

    private static final String TEST_BENCHMARK_ENDPOINT_NAME = "/test";
    private static final String TEST_BENCHMARK_HOST_NAME = "testHostName";
    private static final Integer PORT = 1234;
    @Mock
    private ResponseEntity<BenchmarkResult> resultResponseEntity;
    @MockBean
    private ConnectionSupervisor connectionSupervisor;
    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private DefaultBenchmarkResultCollectorService benchmarkResultCollectorService;

    @Test
    @SuppressWarnings("all")
    void collectAggregatedBenchmarkResults() {
        Long iterations = 3L;
        Benchmark testBenchmark = getTestBenchmark();
        BenchmarkResult testBenchmarkResult = getTestBenchmarkResult();
        when(connectionSupervisor.approveBenchmarking(any(Benchmark.class))).thenReturn(true);
        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(resultResponseEntity);
        when(resultResponseEntity.getBody()).thenReturn(testBenchmarkResult);

        List<AggregatedBenchmarkResult> results =
                benchmarkResultCollectorService.collectAggregatedBenchmarkResults(iterations, testBenchmark);

        double totalTime = testBenchmarkResult.getIndicators().getTimeInSec() * iterations;
        assertEquals(results.size(), testBenchmark.getBenchmarkEndpoints().size());
        assertEquals(results.get(0).getTotalTime(), totalTime, 0.0001d);
        assertNotNull(results.get(0).getLastBenchmarkResult());
        assertEquals(results.get(0).getAverageTime(), totalTime / iterations, 0.0001d);
        verify(connectionSupervisor, times(1)).approveBenchmarking(testBenchmark);
        verify(connectionSupervisor, times(1)).endBenchmarking(testBenchmark);
        verify(restTemplate, times(Math.toIntExact(iterations))).getForEntity(anyString(), any(Class.class));
    }

    @Test
    void shouldReturnEmptyListCollectAggregatedBenchmarkResults() {
        Long iterations = 3L;
        Benchmark testBenchmark = getTestBenchmark();
        when(connectionSupervisor.approveBenchmarking(any(Benchmark.class))).thenReturn(false);

        List<AggregatedBenchmarkResult> results = benchmarkResultCollectorService.collectAggregatedBenchmarkResults(iterations, testBenchmark);

        assertEquals(results, Collections.emptyList());
    }

    private Benchmark getTestBenchmark() {
        return Benchmark.builder()
                .hostName(TEST_BENCHMARK_HOST_NAME)
                .port(PORT)
                .parallelConnections(new AtomicInteger(0))
                .benchmarkEndpoints(List.of(TEST_BENCHMARK_ENDPOINT_NAME))
                .build();
    }

    private BenchmarkResult getTestBenchmarkResult() {
        return BenchmarkResult.builder()
                .endpointName(TEST_BENCHMARK_ENDPOINT_NAME)
                .hostName(TEST_BENCHMARK_HOST_NAME)
                .port(PORT)
                .errors(Errors.builder()
                        .hasErrors(false)
                        .build())
                .indicators(Indicators.builder()
                        .querySize(100L)
                        .timeInSec(10d)
                        .build())
                .build();
    }
}