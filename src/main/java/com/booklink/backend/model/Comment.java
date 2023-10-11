package com.booklink.backend.model;

import com.booklink.backend.dto.comment.CreateCommentDto;
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
public class Comment implements Reactable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "post_id", nullable = false)
    private Long postId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @Column(length = 512)
    private String content;

    private Date createdDate;

    private Date updatedDate;

    private boolean isEdited;

    @ElementCollection
    private List<Long> likes;

    @ElementCollection
    private List<Long> dislikes;


    public static Comment from(CreateCommentDto createCommentDto, Long userId) {
        return Comment.builder()
                .userId(userId)
                .postId(createCommentDto.getPostId())
                .content(createCommentDto.getContent())
                .createdDate(new Date())
                .updatedDate(new Date())
                .isEdited(false)
                .likes(new ArrayList<>())
                .dislikes(new ArrayList<>())
                .build();
    }
}
