package com.grape.data;

import com.grape.domain.Friend;
import com.grape.domain.Post;
import com.grape.domain.PostLike;
import com.grape.repository.FriendRepository;
import com.grape.repository.PostLikeRepository;
import com.grape.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FriendRepository friendRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Value("${dummy.data.size}")
    private Long dummyDataSize;

    @Override
    public void run(String... args) {
        postLikeRepository.deleteAll();
        friendRepository.deleteAll();
        postRepository.deleteAll();
        for (int i = 0; i < dummyDataSize; ++i) {
            Friend friend = Friend.builder()
                    .name("name" + i)
                    .surname("surname" + i)
                    .age(i).build();
            Post post = Post.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .creationDate(LocalDate.now())
                    .build();
            postLikeRepository.save(PostLike.builder()
                    .friend(friend)
                    .post(post)
                    .localDate(LocalDate.now())
                    .build());
        }
    }
}
