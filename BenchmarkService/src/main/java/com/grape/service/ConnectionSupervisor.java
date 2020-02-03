package com.grape.service;

import com.grape.domain.Benchmark;

public interface ConnectionSupervisor {
    boolean approveBenchmarking(Benchmark benchmark);

    void endBenchmarking(Benchmark benchmark);
}
