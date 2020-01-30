package com.grape.controller.rest;

import com.grape.domain.Benchmark;
import com.grape.domain.Friend;
import com.grape.domain.PostLike;
import com.grape.repository.FriendRepository;
import com.grape.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostgreSqlBenchmarkController {

    private final PostLikeRepository postLikeRepository;
    private final FriendRepository friendRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${benchmarkService.registry.name}")
    private String benchmarkServiceRegistryName;

    @GetMapping("/register")
    public ResponseEntity<String> register() {
        Benchmark benchmark = Benchmark.builder()
                .name(applicationName)
                .benchmarkEndpoints(List.of("/likes", "/friends"))
                .build();
        return restTemplate.postForEntity(
                String.format("http://%s/register", benchmarkServiceRegistryName),
                benchmark,
                String.class
        );
    }

    @GetMapping("/likes")
    public ResponseEntity<String> getAllLikes() {
        long start = System.currentTimeMillis();
        List<PostLike> postLikes = postLikeRepository.findAll();
        return ResponseEntity.ok(String.format("Execution time, Endpoint [/likes] {%s}: %.5f s [size: %d]",
                applicationName,
                (System.currentTimeMillis() - start) / 1000d,
                postLikes.size())
        );
    }

    @GetMapping("/friends")
    public ResponseEntity<String> getAllFriends() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        return ResponseEntity.ok(String.format("Execution time, Endpoint [/friends] {%s}: %.5f s [size: %d]",
                applicationName,
                (System.currentTimeMillis() - start) / 1000d,
                friends.size())
        );
    }
}
