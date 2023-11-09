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
public class LatestPostDto {
    public Long id;
    public Long forumId;
    public String forumName;
    public String img;
    public String authorName;
    public String content;
    public Date createdDate;
    public Date updatedDate;

    public static LatestPostDto from(Post post) {
        return LatestPostDto.builder()
                .id(post.getId())
                .forumId(post.getForumId())
                .forumName(post.getForum().getName())
                .img(post.getForum().getImg())
                .authorName(post.getUser().getUsername())
                .content("creó una publicación en")
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .build();
    }



}
