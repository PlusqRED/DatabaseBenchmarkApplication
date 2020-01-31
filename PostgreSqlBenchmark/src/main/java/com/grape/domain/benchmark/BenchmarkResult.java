package com.grape.domain.benchmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenchmarkResult {
    private String hostName;
    private Integer port;
    private String endpointName;
    private Indicators indicators;
    private Errors errors;
}
