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
    private Long user_id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    private String description;

    private String img;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<User> member_list;

    public static Forum from(CreateForumDto forumDto) {
        return Forum.builder()
                .id(forumDto.getUserId())
                .name(forumDto.getName())
                .user_id(forumDto.getUserId())
                .description(forumDto.getDescription())
                .img(forumDto.getImg())
                .member_list(new ArrayList<>())
                .build();
    }
}
