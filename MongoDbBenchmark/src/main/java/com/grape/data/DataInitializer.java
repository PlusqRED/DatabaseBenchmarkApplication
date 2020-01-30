package com.grape.data;

import com.grape.domain.Friend;
import com.grape.domain.Post;
import com.grape.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FriendRepository friendRepository;

    @Override
    public void run(String... args) throws Exception {
        friendRepository.deleteAll();
        Friend friend = Friend.builder()
                .name("Oleg")
                .surnmae("Vinograd")
                .age(20)
                .build();
        List<Post> posts = List.of(Post.builder()
                .title("Hello")
                .content("World")
                .build());
        friend.setLikedPosts(posts);
        friendRepository.save(friend);
    }

}
