package com.grape.data;

import com.grape.domain.Friend;
import com.grape.domain.Post;
import com.grape.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FriendRepository friendRepository;

    @Value("${dummy.data.size}")
    private Long dummyDataSize;

    @Override
    public void run(String... args) {
        friendRepository.deleteAll();
        for (int i = 0; i < dummyDataSize; ++i) {
            Friend friend = Friend.builder()
                    .name("name" + i)
                    .surnmae("surname" + i)
                    .age(i)
                    .build();
            List<Post> posts = List.of(Post.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .creationDate(LocalDate.now())
                    .build());
            friend.setLikedPosts(posts);
            friendRepository.save(friend);
        }
    }

}
