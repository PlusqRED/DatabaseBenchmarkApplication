package com.grape.service.impl;

import com.grape.domain.Benchmark;
import com.grape.service.ConnectionSupervisor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParallelConnectionSupervisor implements ConnectionSupervisor {

    @Value("${benchmark.max.parallel.connections}")
    private Integer maxParallelConnections;

    @Override
    public boolean approveBenchmarking(Benchmark benchmark) {
        if (benchmark.getParallelConnections().get() < maxParallelConnections) {
            benchmark.getParallelConnections().incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public void endBenchmarking(Benchmark benchmark) {
        if (benchmark.getParallelConnections().get() != 0) {
            benchmark.getParallelConnections().decrementAndGet();
        }
    }
}
