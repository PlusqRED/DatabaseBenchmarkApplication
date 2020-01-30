package com.grape.controller.rest;

import com.grape.domain.Friend;
import com.grape.domain.Post;
import com.grape.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class MongoDbBenchmarkController {

    private final FriendRepository friendRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping("/likes")
    public ResponseEntity<String> getAllLikes() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return ResponseEntity.ok(String.format("Execution time {%s:%d} Endpoint [/likes]: %.5f s [size: %d]",
                applicationName,
                serverPort,
                resultSeconds,
                friends.size()
        ));
    }

    @GetMapping("/friends")
    public ResponseEntity<String> getAllFriends() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        List<Post> likedPosts = friends.stream().flatMap(friend -> friend.getLikedPosts().stream()).collect(toList());
        double resultSeconds = (System.currentTimeMillis() - start) / 1000d;
        return ResponseEntity.ok(String.format("Execution time {%s:%d} Endpoint [/friends]: %.5f s [size: %d]",
                applicationName,
                serverPort,
                resultSeconds,
                likedPosts.size()
        ));
    }
}