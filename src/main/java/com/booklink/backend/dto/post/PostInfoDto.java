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
public class PostInfoDto {
    private Long id;
    private UserPostDto user;
    private String content;
    private Date date;
    private int commentsCount;

    public static PostInfoDto from(Post post) {
        return PostInfoDto.builder()
                .id(post.getId())
                .user(UserPostDto.from(post.getUser()))
                .content(post.getContent())
                .date(post.getCreatedDate())
                .commentsCount(post.getComments().size())
                .build();
    }
}
