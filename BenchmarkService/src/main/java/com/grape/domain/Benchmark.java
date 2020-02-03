package com.grape.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Benchmark {
    private String hostName;
    private Integer port;
    private List<String> benchmarkEndpoints;
    private AtomicInteger parallelConnections = new AtomicInteger(0);
}