package com.grape.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RelationshipEntity
public class Like {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate creationDate;

    @StartNode
    private Friend friend;

    @EndNode
    private Post post;
}
