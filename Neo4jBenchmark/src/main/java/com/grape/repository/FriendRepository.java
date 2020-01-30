package com.grape.repository;

import com.grape.domain.Friend;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FriendRepository extends Neo4jRepository<Friend, Long> {
}
