package com.grape.data;

import com.grape.domain.Friend;
import com.grape.domain.Like;
import com.grape.domain.Post;
import com.grape.repository.FriendRepository;
import com.grape.repository.LikeRepository;
import com.grape.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final FriendRepository friendRepository;
    private final LikeRepository likeRepository;

    @Value("${dummy.data.size}")
    private Long dummyDataSize;

    @Override
    public void run(String... args) {
        postRepository.deleteAll();
        friendRepository.deleteAll();
        likeRepository.deleteAll();
        for (int i = 0; i < dummyDataSize; ++i) {
            Friend friend = friendRepository.save(Friend.builder()
                    .name("name" + i)
                    .surname("surname" + i)
                    .age(i)
                    .build());
            Post post = postRepository.save(Post.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .creationDate(LocalDate.now())
                    .build());
            Like like = Like.builder()
                    .friend(friend)
                    .post(post)
                    .build();
            likeRepository.save(like);
        }
    }
}
