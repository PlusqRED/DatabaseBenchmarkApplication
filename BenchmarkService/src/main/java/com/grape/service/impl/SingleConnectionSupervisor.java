package com.grape.service.impl;

import com.grape.domain.Benchmark;
import com.grape.service.ConnectionSupervisor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleConnectionSupervisor implements ConnectionSupervisor {

    @Override
    public boolean approveBenchmarking(Benchmark benchmark) {
        if(benchmark.getParallelConnections().get() == 0) {
            benchmark.getParallelConnections().incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public void endBenchmarking(Benchmark benchmark) {
        if(benchmark.getParallelConnections().get() != 0) {
            benchmark.getParallelConnections().decrementAndGet();
        }
    }
}
