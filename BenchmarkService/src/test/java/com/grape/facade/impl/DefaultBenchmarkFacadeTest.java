package com.grape.facade.impl;

import com.grape.domain.Benchmark;
import com.grape.domain.BenchmarkPool;
import com.grape.service.BenchmarkResultCollectorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.util.Assert.notNull;

@RunWith(SpringRunner.class)
@WebMvcTest(DefaultBenchmarkFacade.class)
public class DefaultBenchmarkFacadeTest {

    private final String TEST_BENCHMARK_ENDPOINT_NAME = "/test";
    private final String TEST_BENCHMARK_HOST_NAME = "testHostName";
    private final Integer PORT = 1234;
    private final Map<String, Benchmark> benchmarkList = getBenchmarkList();
    @MockBean
    private BenchmarkPool benchmarkPool;
    @MockBean
    private BenchmarkResultCollectorService benchmarkResultCollectorService;
    @Autowired
    private DefaultBenchmarkFacade defaultBenchmarkFacade;

    @Before
    public void initialize() {
        when(benchmarkPool.getBenchmarkList()).thenReturn(benchmarkList);
    }

    @Test
    @SuppressWarnings("all")
    public void shouldFindBenchmark() {
        Benchmark testBenchmark = benchmarkList.values().stream()
                .filter(value -> value.getHostName().equalsIgnoreCase(TEST_BENCHMARK_HOST_NAME))
                .findAny()
                .get();

        Benchmark benchmark = defaultBenchmarkFacade.findBenchmark(testBenchmark.getHostName());

        notNull(benchmark, "Benchmark was supposed to be found!");
        Assert.assertEquals(benchmark, testBenchmark);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotFindBenchmark() {
        defaultBenchmarkFacade.findBenchmark("c");
    }

    @Test
    public void benchmarkAllWithIterations() {
        Long iterations = 3L;

        defaultBenchmarkFacade.benchmarkAllWithIterations(iterations);

        verify(benchmarkResultCollectorService, times(benchmarkList.size()))
                .collectAggregatedBenchmarkResults(eq(iterations), any(Benchmark.class));
    }

    @Test
    public void benchmarkWithIterations() {
        Long iterations = 3L;

        Benchmark testBenchmark = getTestBenchmark();
        defaultBenchmarkFacade.benchmarkWithIterations(testBenchmark, iterations);

        verify(benchmarkResultCollectorService, times(1))
                .collectAggregatedBenchmarkResults(iterations, testBenchmark);
    }

    @Test
    @SuppressWarnings("all")
    public void registerBenchmark() {
        Benchmark testBenchmark = getTestBenchmark();
        Map<String, Benchmark> benchmarkList = mock(Map.class);
        when(benchmarkPool.getBenchmarkList()).thenReturn(benchmarkList);

        defaultBenchmarkFacade.registerBenchmark(testBenchmark);

        verify(benchmarkPool.getBenchmarkList(), times(1))
                .put(anyString(), eq(testBenchmark));
    }

    private Map<String, Benchmark> getBenchmarkList() {
        return new HashMap<>() {{
            put("a", getTestBenchmark());
            put("b", getTestBenchmark());
        }};
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