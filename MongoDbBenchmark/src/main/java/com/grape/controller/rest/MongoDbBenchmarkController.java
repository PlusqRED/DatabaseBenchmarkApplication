package com.grape.controller.rest;

import com.grape.domain.Friend;
import com.grape.domain.Post;
import com.grape.domain.benchmark.BenchmarkResult;
import com.grape.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class MongoDbBenchmarkController {

    private final FriendRepository friendRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    private static final String LIKES_ENDPOINT = "/likes";
    private static final String FRIENDS_ENDPOINT = "/friends";

    @GetMapping(FRIENDS_ENDPOINT)
    public ResponseEntity<BenchmarkResult> getAllLikes() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return getBenchmarkResultResponseEntity(FRIENDS_ENDPOINT, resultSeconds, friends.size());
    }

    @GetMapping(LIKES_ENDPOINT)
    public ResponseEntity<BenchmarkResult> getAllFriends() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        List<Post> likedPosts = friends.stream().flatMap(friend -> friend.getLikedPosts().stream()).collect(toList());
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return getBenchmarkResultResponseEntity(LIKES_ENDPOINT, resultSeconds, likedPosts.size());
    }

    private ResponseEntity<BenchmarkResult> getBenchmarkResultResponseEntity(String endpointName, double resultSeconds, int size) {
        return ResponseEntity.ok(BenchmarkResult.builder()
                .hostName(applicationName)
                .endpointName(endpointName)
                .port(serverPort)
                .querySize((long) size)
                .timeInSec(resultSeconds)
                .build());
    }
}
