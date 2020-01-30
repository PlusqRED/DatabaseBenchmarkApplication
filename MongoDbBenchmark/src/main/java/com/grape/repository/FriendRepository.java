package com.grape.repository;

import com.grape.domain.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendRepository extends MongoRepository<Friend, Long> {
}
