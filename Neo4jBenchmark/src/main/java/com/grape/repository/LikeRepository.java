package com.grape.repository;

import com.grape.domain.Like;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface LikeRepository extends Neo4jRepository<Like, Long> {
}
