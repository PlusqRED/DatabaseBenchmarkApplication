package com.grape.controller.rest;

import com.grape.domain.Friend;
import com.grape.domain.PostLike;
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

    private final PostLikeRepository postLikeRepository;
    private final FriendRepository friendRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping("/likes")
    public ResponseEntity<String> getAllLikes() {
        long start = System.currentTimeMillis();
        List<PostLike> postLikes = postLikeRepository.findAll();
        return ResponseEntity.ok(String.format("Execution time {%s:%d} Endpoint [/likes] : %.5f s [size: %d]",
                applicationName,
                serverPort,
                (System.currentTimeMillis() - start) / 1000d,
                postLikes.size())
        );
    }

    @GetMapping("/friends")
    public ResponseEntity<String> getAllFriends() {
        long start = System.currentTimeMillis();
        List<Friend> friends = friendRepository.findAll();
        return ResponseEntity.ok(String.format("Execution time {%s:%d} Endpoint [/friends]: %.5f s [size: %d]",
                applicationName,
                serverPort,
                (System.currentTimeMillis() - start) / 1000d,
                friends.size())
        );
    }
}
