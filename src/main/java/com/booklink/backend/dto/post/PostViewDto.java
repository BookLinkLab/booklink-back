package com.booklink.backend.dto.post;

import com.booklink.backend.dto.comment.CommentViewDto;
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
    Date createdDate;
    boolean isEdited;
    Date updatedDate;
    List<Long> likes;
    List<Long> dislikes;
    List<CommentViewDto> comments;

    public static PostViewDto from(Post post) {
        return PostViewDto.builder()
                .id(post.getId())
                .user(UserPostDto.from(post.getUser()))
                .forumId(post.getForumId())
                .forumName(post.getForum().getName())
                .content(post.getContent())
                .createdDate(post.getCreatedDate())
                .isEdited(post.isEdited())
                .updatedDate(post.getUpdatedDate())
                .likes(post.getLikes())
                .dislikes(post.getDislikes())
                .comments(post.getComments().stream().map(CommentViewDto::from).toList())
                .build();
    }
}
