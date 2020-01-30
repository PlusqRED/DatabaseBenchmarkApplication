package com.grape.controller.rest;

import com.grape.domain.Friend;
import com.grape.domain.Like;
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

    @GetMapping("/likes")
    public ResponseEntity<String> getAllLikes() {
        long start = System.currentTimeMillis();
        Iterable<Like> likes = likeRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return ResponseEntity.ok(String.format("Execution time {%s:%d} Endpoint [/likes]: %.5f s [size: %d]",
                applicationName,
                serverPort,
                resultSeconds,
                (int) StreamSupport.stream(likes.spliterator(), false).count()
        ));
    }

    @GetMapping("/friends")
    public ResponseEntity<String> getAllFriends() {
        long start = System.currentTimeMillis();
        Iterable<Friend> friends = friendRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return ResponseEntity.ok(String.format("Execution time {%s:%d} Endpoint [/friends]: %.5f s [size: %d]",
                applicationName,
                serverPort,
                resultSeconds,
                (int) StreamSupport.stream(friends.spliterator(), false).count()
        ));
    }
}
