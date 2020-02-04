package com.grape.controller.rest.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice(basePackages = {"com.grape.controller.rest"})
public class GlobalControllerAdvice {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> handleError(Exception ex) {
        return ResponseEntity.ok(ex.getMessage());
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
