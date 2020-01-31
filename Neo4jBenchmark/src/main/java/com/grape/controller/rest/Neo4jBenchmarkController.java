package com.grape.controller.rest;

import com.grape.domain.Friend;
import com.grape.domain.Like;
import com.grape.domain.benchmark.BenchmarkResult;
import com.grape.domain.benchmark.Errors;
import com.grape.domain.benchmark.Indicators;
import com.grape.repository.FriendRepository;
import com.grape.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
public class Neo4jBenchmarkController {

    private final LikeRepository likeRepository;
    private final FriendRepository friendRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    private static final String LIKES_ENDPOINT = "/likes";
    private static final String FRIENDS_ENDPOINT = "/friends";

    @GetMapping(LIKES_ENDPOINT)
    public ResponseEntity<BenchmarkResult> getAllLikes() {
        long start = System.currentTimeMillis();
        Iterable<Like> likes = likeRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return getBenchmarkResultResponseEntity(likes, resultSeconds, LIKES_ENDPOINT);
    }

    @GetMapping(FRIENDS_ENDPOINT)
    public ResponseEntity<BenchmarkResult> getAllFriends() {
        long start = System.currentTimeMillis();
        Iterable<Friend> friends = friendRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return getBenchmarkResultResponseEntity(friends, resultSeconds, FRIENDS_ENDPOINT);
    }

    private ResponseEntity<BenchmarkResult> getBenchmarkResultResponseEntity(Iterable<?> likes, double resultSeconds, String endpointName) {
        return ResponseEntity.ok(BenchmarkResult.builder()
                .hostName(applicationName)
                .endpointName(endpointName)
                .port(serverPort)
                .indicators(Indicators.builder()
                        .querySize(StreamSupport.stream(likes.spliterator(), false).count())
                        .timeInSec(resultSeconds)
                        .build())
                .errors(Errors.builder().hasErrors(false).build())
                .build());
    }
}
