package com.booklink.backend.dto.comment;

import com.booklink.backend.model.Comment;
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
public class CommentDto {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private Date createdDate;
    private Date updatedDate;
    private boolean isEdited;
    private List<Long> likes;
    private List<Long> dislikes;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .updatedDate(comment.getUpdatedDate())
                .isEdited(comment.isEdited())
                .likes(comment.getLikes())
                .dislikes(comment.getDislikes())
                .build();
    }
}
