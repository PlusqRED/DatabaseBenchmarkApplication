package com.grape.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BenchmarkInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> data = new HashMap<>();
        data.put("benchmark.service.version", "0.0.1");
        data.put("description", "Executes http requests and aggregates information as report");
        builder.withDetail("buildInfo", data);
    }

}