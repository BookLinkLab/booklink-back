package com.booklink.backend.service.impl;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.Comment;
import com.booklink.backend.repository.CommentRepository;
import com.booklink.backend.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto createComment(CreateCommentDto createCommentDto, Long userId) {
        Comment commentToSave = Comment.from(createCommentDto, userId);
        Comment savedComment = commentRepository.save(commentToSave);
        return CommentDto.from(savedComment);
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        Comment comment = commentOptional.orElseThrow(() -> new NotFoundException("El comentario no fue encontrado"));
        return CommentDto.from(comment);
    }
}
