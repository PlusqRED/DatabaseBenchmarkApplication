package com.grape.domain.benchmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Benchmark {
    private String hostName;
    private Integer port;
    private List<String> benchmarkEndpoints;
}
