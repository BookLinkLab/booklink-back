package com.booklink.backend.dto.post;

import com.booklink.backend.dto.comment.CommentDto;
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
public class PostViewDto {
    Long id;
    UserPostDto user;
    Long forumId;
    String forumName;
    String content;
    Date date;
    List<CommentDto> comments;

    public static PostViewDto from(Post post) {
        return PostViewDto.builder()
                .id(post.getId())
                .user(UserPostDto.from(post.getUser()))
                .forumId(post.getForumId())
                .forumName(post.getForum().getName())
                .content(post.getContent())
                .date(post.getCreatedDate())
                .comments(post.getComments().stream().map(CommentDto::from).toList())
                .build();
    }
}
