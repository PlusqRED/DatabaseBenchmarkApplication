package com.grape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class BenchmarkDiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BenchmarkDiscoveryServerApplication.class, args);
    }

}
