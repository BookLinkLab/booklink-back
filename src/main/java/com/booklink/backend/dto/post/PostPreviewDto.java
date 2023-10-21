package com.booklink.backend.dto.post;

import com.booklink.backend.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPreviewDto {
    private Long id;
    private Long forumId;
    private String forumName;
    private String content;

    public static PostPreviewDto from(Post post) {
        return PostPreviewDto.builder()
                .id(post.getId())
                .forumId(post.getForum().getId())
                .forumName(post.getForum().getName())
                .content(post.getContent())
                .build();
    }

}
