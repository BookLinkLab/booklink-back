package com.booklink.backend.dto.post;

import com.booklink.backend.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String content;
    private Long userId;
    private Long forumId;
    private Date createdDate;
    private boolean isEdited;
    private Date updatedDate;
    private List<Long> likes;

    public static PostDto from(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .userId(post.getUserId())
                .forumId(post.getForumId())
                .createdDate(post.getCreatedDate())
                .isEdited(post.isEdited())
                .updatedDate(post.getUpdatedDate())
                .likes(post.getLikes())
                .build();
    }
}
