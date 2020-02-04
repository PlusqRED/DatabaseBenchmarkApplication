package com.grape.controller.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grape.controller.rest.BenchmarkController;
import com.grape.domain.*;
import com.grape.facade.BenchmarkFacade;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RestBenchmarkControllerIntegrationTest {

    private static final String TEST_BENCHMARK_ENDPOINT_NAME = "/test";
    private static final String TEST_BENCHMARK_HOST_NAME = "testHostName";
    private static final Integer PORT = 1234;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BenchmarkFacade benchmarkFacade;
    @Value("${iterations.error.message.negative}")
    private String iterationsErrorMessageNegative;
    @Value("${benchmark.error.message.notFound}")
    private String benchmarkErrorMessageNotFound;

    @Test
    public void benchmarkAllOk() throws Exception {
        Long iterations = 3L;

        mockMvc.perform(
                get("/benchmarks").param(RestBenchmarkController.ITERATIONS, String.valueOf(iterations)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));

        verify(benchmarkFacade, times(1))
                .benchmarkAllWithIterations(iterations);
    }

    @Test
    public void benchmarkAllFail() throws Exception {
        Long iterations = -3L;
        String errorMessage = HttpStatus.BAD_REQUEST.value() + " " + iterationsErrorMessageNegative;

        mockMvc.perform(
                get("/benchmarks").param(RestBenchmarkController.ITERATIONS, String.valueOf(iterations)))
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(benchmarkFacade, never())
                .benchmarkAllWithIterations(iterations);
    }

    @Test
    public void benchmarkOk() throws Exception {
        Long iterations = 3L;
        Benchmark testBenchmark = getTestBenchmark();
        Map<String, List<AggregatedBenchmarkResult>> aggregatedResults = getAggregatedResults(iterations, testBenchmark);
        when(benchmarkFacade.findBenchmark(testBenchmark.getHostName())).thenReturn(testBenchmark);
        when(benchmarkFacade.benchmarkWithIterations(testBenchmark, iterations)).thenReturn(aggregatedResults);
        JSONObject expectedResult = new JSONObject(aggregatedResults);

        MvcResult mvcResult = mockMvc.perform(
                get("/benchmarks/" + testBenchmark.getHostName())
                        .param(BenchmarkController.ITERATIONS, String.valueOf(iterations)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals(expectedResult.toJSONString(), mvcResult.getResponse().getContentAsString(), true);
        verify(benchmarkFacade, times(1))
                .benchmarkWithIterations(testBenchmark, iterations);
    }

    @Test
    public void benchmarkFail() throws Exception {
        Long iterations = -3L;
        String errorMessage = HttpStatus.BAD_REQUEST.value() + " " + iterationsErrorMessageNegative;
        Benchmark testBenchmark = getTestBenchmark();
        Map<String, List<AggregatedBenchmarkResult>> aggregatedResults = getAggregatedResults(iterations, testBenchmark);
        when(benchmarkFacade.findBenchmark(testBenchmark.getHostName())).thenReturn(testBenchmark);
        when(benchmarkFacade.benchmarkWithIterations(testBenchmark, iterations)).thenReturn(aggregatedResults);

        mockMvc.perform(
                get("/benchmarks/" + testBenchmark.getHostName())
                        .param(BenchmarkController.ITERATIONS, String.valueOf(iterations)))
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(benchmarkFacade, never()).findBenchmark(testBenchmark.getHostName());
        verify(benchmarkFacade, never()).benchmarkWithIterations(testBenchmark, iterations);
    }

    @Test
    public void registerBenchmarkTest() throws Exception {
        Benchmark testBenchmark = getTestBenchmark();
        String message = "Successfully registered!";
        mockMvc.perform(
                post("/benchmarks/register")
                        .content(new ObjectMapper().writeValueAsBytes(testBenchmark))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(message));

        verify(benchmarkFacade, times(1)).registerBenchmark(any(Benchmark.class));
    }

    private Map<String, List<AggregatedBenchmarkResult>> getAggregatedResults(Long iterations, Benchmark testBenchmark) {
        return new HashMap<>() {{
            double totalTime = 30d;
            put(testBenchmark.getHostName(), List.of(AggregatedBenchmarkResult.builder()
                    .iterations(iterations)
                    .totalTime(totalTime)
                    .averageTime(totalTime / iterations)
                    .lastBenchmarkResult(BenchmarkResult.builder()
                            .indicators(Indicators.builder()
                                    .timeInSec(totalTime / iterations)
                                    .querySize(100L)
                                    .build())
                            .errors(Errors.builder()
                                    .hasErrors(false)
                                    .build())
                            .build())
                    .build()));
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
