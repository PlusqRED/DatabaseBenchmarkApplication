package com.grape.controller.rest.handler;

import com.grape.domain.benchmark.BenchmarkResult;
import com.grape.domain.benchmark.Errors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice(basePackages = {"com.grape.controller.rest"})
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BenchmarkResult> handleError(Exception ex) {
        return ResponseEntity.ok(BenchmarkResult.builder()
                .errors(Errors.builder()
                        .hasErrors(true)
                        .message(ex.getMessage())
                        .stackTrace(Arrays.toString(ex.getStackTrace()))
                        .build())
                .build());
    }
}
