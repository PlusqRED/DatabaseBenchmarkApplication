package com.grape.controller.rest.impl;

import com.grape.domain.Benchmark;
import com.grape.facade.BenchmarkFacade;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RestBenchmarkController.class)
class RestBenchmarkControllerUnitTest {

    private static final String TEST_BENCHMARK_ENDPOINT_NAME = "/test";
    private static final String TEST_BENCHMARK_HOST_NAME = "testHostName";
    private static final Integer PORT = 1234;
    @MockBean
    private BenchmarkFacade benchmarkFacade;
    @Autowired
    private RestBenchmarkController restBenchmarkController;
    @Value("${iterations.error.message.negative}")
    private String iterationsErrorMessageNegative;

    @Test
    void benchmarkAll() {
        Long iterations = 3L;

        restBenchmarkController.benchmarkAll(iterations);

        verify(benchmarkFacade, times(1))
                .benchmarkAllWithIterations(iterations);
    }

    @Test
    void registerBenchmark() {
        Benchmark testBenchmark = getTestBenchmark();

        restBenchmarkController.registerBenchmark(testBenchmark);

        verify(benchmarkFacade, times(1))
                .registerBenchmark(testBenchmark);
    }

    @Test
    void benchmark() {
        Long iterations = 3L;
        Benchmark testBenchmark = getTestBenchmark();
        when(benchmarkFacade.findBenchmark(anyString())).thenReturn(testBenchmark);

        restBenchmarkController.benchmark(testBenchmark.getHostName(), iterations);

        verify(benchmarkFacade, times(1)).findBenchmark(testBenchmark.getHostName());
        verify(benchmarkFacade, times(1))
                .benchmarkWithIterations(testBenchmark, iterations);
    }

    private Benchmark getTestBenchmark() {
        return Benchmark.builder()
                .hostName(TEST_BENCHMARK_HOST_NAME)
                .port(PORT)
                .parallelConnections(new AtomicInteger(0))
                .benchmarkEndpoints(List.of(TEST_BENCHMARK_ENDPOINT_NAME))
                .build();
    }
}