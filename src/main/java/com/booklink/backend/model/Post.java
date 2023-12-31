package com.booklink.backend.model;

import com.booklink.backend.dto.post.CreatePostDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements Reactable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "forum_id", nullable = false)
    private Long forumId;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "forum_id", insertable = false, updatable = false)
    private Forum forum;

    @Column(length = 512)
    private String content;

    private Date createdDate;

    private Date updatedDate;

    private boolean isEdited;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ElementCollection
    private List<Long> likes;

    @ElementCollection
    private List<Long> dislikes;

    public static Post from(CreatePostDto postDto, Long userId) {
        return Post.builder()
                .userId(userId)
                .forumId(postDto.getForumId())
                .content(postDto.getContent())
                .createdDate(new Date())
                .isEdited(false)
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .dislikes(new ArrayList<>())
                .build();
    }
}
