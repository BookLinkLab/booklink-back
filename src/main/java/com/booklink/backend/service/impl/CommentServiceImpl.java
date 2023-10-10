package com.booklink.backend.service.impl;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.comment.EditCommentDto;
import com.booklink.backend.dto.forum.ForumDtoFactory;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.exception.*;
import com.booklink.backend.model.Comment;
import com.booklink.backend.repository.CommentRepository;
import com.booklink.backend.service.CommentService;
import com.booklink.backend.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final ForumDtoFactory forumDtoFactory;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService, ForumDtoFactory forumDtoFactory) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.forumDtoFactory = forumDtoFactory;
    }

    @Override
    public CommentDto createComment(CreateCommentDto createCommentDto, Long userId) {
        PostDto postDto = postService.getPostById(createCommentDto.getPostId());
        boolean isMember = forumDtoFactory.isMember(postDto.getForumId(), userId);

        if (!isMember) throw new MemberDoesntBelongForumException("No puedes comentar en este posteo");

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

    public Comment getCommentEntityById(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        return commentOptional.orElseThrow(() -> new NotFoundException("El comentario no fue encontrado"));
    }
    @Override
    public void deleteComment(Long id, Long userId) {
        Comment comment = getCommentEntityById(id);
        Long forumid = comment.getPost().getForumId();

        boolean isCommentCreator = comment.getUserId().equals(userId);
        boolean isForumOwner = forumDtoFactory.isForumOwner(forumid, userId);

        if (!isCommentCreator && !isForumOwner) throw new UserNotOwnerException("No tienes permiso para eliminar este comentario");

        commentRepository.deleteById(id);
    }

    @Override
    public CommentDto editComment(Long id, EditCommentDto editCommentDto, Long userId) {
        Comment comment = getCommentEntityById(id);

        boolean isCommentCreator = comment.getUserId().equals(userId);

        if (!isCommentCreator) throw new UserNotOwnerException("No tienes permiso para editar este comentario");


        boolean isEdited = !comment.getContent().equals(editCommentDto.getContent()) && !editCommentDto.getContent().isEmpty();

        if (isEdited) {
            comment.setEdited(true);
            comment.setUpdatedDate(new Date());
            comment.setContent(editCommentDto.getContent());
            Comment savedComment = commentRepository.save(comment);
            return CommentDto.from(savedComment);
        }
        return CommentDto.from(comment);
    }
}
