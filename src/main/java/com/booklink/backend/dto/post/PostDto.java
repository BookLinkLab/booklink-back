package com.booklink.backend.dto.post;

import com.booklink.backend.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Date editedDate;

    public static PostDto from(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .userId(post.getUserId())
                .forumId(post.getForumId())
                .createdDate(post.getCreatedDate())
                .editedDate(post.getUpdatedDate())
                .isEdited(post.isEdited())
                .build();
    }
}
