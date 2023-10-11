package com.booklink.backend.service;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.comment.EditCommentDto;

public interface CommentService {
    CommentDto createComment(CreateCommentDto createCommentDto, Long userId);

    CommentDto getCommentById(Long id);

    void deleteComment(Long id, Long userId);

    CommentDto editComment(Long id, EditCommentDto editCommentDto, Long userId);

    CommentDto toggleLike(Long id, Long userId);
}
