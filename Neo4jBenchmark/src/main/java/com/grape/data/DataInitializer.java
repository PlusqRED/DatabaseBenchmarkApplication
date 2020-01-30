package com.grape.data;

import com.grape.domain.Friend;
import com.grape.domain.Like;
import com.grape.domain.Post;
import com.grape.repository.FriendRepository;
import com.grape.repository.LikeRepository;
import com.grape.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final FriendRepository friendRepository;
    private final LikeRepository likeRepository;

    @Override
    public void run(String... args) throws Exception {
        postRepository.deleteAll();
        Friend friend = Friend.builder().name("Oleg").surname("Vinograd").age(20).build();
        Post post = Post.builder().title("title").content("content").build();
        Like like = Like.builder().friend(friend).post(post).build();
        likeRepository.save(like);
        postRepository.save(post);
    }
}
