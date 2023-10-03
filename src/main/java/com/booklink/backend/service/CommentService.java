package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;

public interface CommentService {
    CommentDto createComment(CreateCommentDto createCommentDto, Long userId);

    CommentDto getCommentById(Long id);
}
