package com.booklink.backend.dto.comment;

import com.booklink.backend.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .build();
    }
}
