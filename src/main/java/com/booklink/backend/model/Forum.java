package com.booklink.backend.model;

import com.booklink.backend.dto.forum.CreateForumDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(length = 512)
    private String description;

    private String img;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<User> members;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<Tag> tags;

    private int membersAmount;

    public static Forum from(CreateForumDto forumDto, Long userId) {
        return Forum.builder()
                .name(forumDto.getName())
                .userId(userId)
                .description(forumDto.getDescription())
                .img(forumDto.getImg())
                .members(new ArrayList<>())
                .membersAmount(1)
                .tags(new ArrayList<>())
                .build();
    }

}
