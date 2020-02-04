package com.grape.service.impl;

import com.grape.domain.Benchmark;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;


@RunWith(SpringRunner.class)
@WebMvcTest(ParallelConnectionSupervisor.class)
@TestPropertySource(properties = {
        "benchmark.max.parallel.connections=1",
})
class ParallelConnectionSupervisorTest {

    @Autowired
    private ParallelConnectionSupervisor parallelConnectionSupervisor;

    @Test
    void approveBenchmarking() {
        Benchmark benchmark = getBenchmark(0);

        boolean isApproved = parallelConnectionSupervisor.approveBenchmarking(benchmark);

        Assert.assertTrue("Benchmarking should be approved", isApproved);
    }

    @Test
    void blockBenchmarking() {
        Benchmark benchmark = getBenchmark(1);

        boolean isApproved = parallelConnectionSupervisor.approveBenchmarking(benchmark);

        Assert.assertFalse("Benchmarking should be blocked", isApproved);
    }

    @Test
    void decrementBenchmark() {
        Benchmark benchmark = getBenchmark(1);

        parallelConnectionSupervisor.endBenchmarking(benchmark);

        Assert.assertEquals("Benchmark connections was supposed to decrease",
                0,
                benchmark.getParallelConnections().get());
    }

    @Test
    void skipEndBenchmarking() {
        Benchmark benchmark = getBenchmark(0);

        parallelConnectionSupervisor.endBenchmarking(benchmark);

        Assert.assertEquals("Benchmark connections was not supposed to change",
                0,
                benchmark.getParallelConnections().get());
    }

    private Benchmark getBenchmark(int parallelConnections) {
        return Benchmark.builder()
                .parallelConnections(new AtomicInteger(parallelConnections))
                .build();
    }
}