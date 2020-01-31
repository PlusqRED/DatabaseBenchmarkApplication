package com.grape.controller.rest;

import com.grape.domain.Friend;
import com.grape.domain.PostLike;
import com.grape.domain.benchmark.BenchmarkResult;
import com.grape.domain.benchmark.Errors;
import com.grape.domain.benchmark.Indicators;
import com.grape.repository.FriendRepository;
import com.grape.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostgreSqlBenchmarkController {

    private static final String LIKES_ENDPOINT = "/likes";
    private static final String FRIENDS_ENDPOINT = "/friends";
    private final PostLikeRepository postLikeRepository;
    private final FriendRepository friendRepository;
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping(LIKES_ENDPOINT)
    public ResponseEntity<BenchmarkResult> getAllLikes() {
        long start = System.currentTimeMillis();
        List<PostLike> postLikes = postLikeRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return getBenchmarkResultResponseEntity(resultSeconds, LIKES_ENDPOINT, postLikes.size());
    }

    @GetMapping(FRIENDS_ENDPOINT)
    public ResponseEntity<BenchmarkResult> getAllFriends() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return getBenchmarkResultResponseEntity(resultSeconds, FRIENDS_ENDPOINT, friends.size());
    }

    private ResponseEntity<BenchmarkResult> getBenchmarkResultResponseEntity(double resultSeconds, String endpointName, int size) {
        return ResponseEntity.ok(BenchmarkResult.builder()
                .hostName(applicationName)
                .endpointName(endpointName)
                .port(serverPort)
                .indicators(Indicators.builder()
                        .timeInSec(resultSeconds)
                        .querySize((long) size)
                        .build())
                .errors(Errors.builder().hasErrors(false).build())
                .build());
    }
}
