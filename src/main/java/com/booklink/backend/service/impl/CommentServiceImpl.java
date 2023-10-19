package com.booklink.backend.service.impl;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.comment.EditCommentDto;
import com.booklink.backend.dto.forum.ForumDtoFactory;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.exception.*;
import com.booklink.backend.model.Comment;
import com.booklink.backend.model.Post;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.CommentRepository;
import com.booklink.backend.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final ForumService forumService;
    private final NotificationService notificationService;
    private final ForumDtoFactory forumDtoFactory;
    private final ReactionService<Comment> reactionService;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService, ForumService forumService, NotificationService notificationService, ForumDtoFactory forumDtoFactory, ReactionService<Comment> reactionService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.forumService = forumService;
        this.notificationService = notificationService;
        this.forumDtoFactory = forumDtoFactory;
        this.reactionService = reactionService;
    }

    @Override
    public CommentDto createComment(CreateCommentDto createCommentDto, Long userId) {
        Post post = postService.getPostEntity(createCommentDto.getPostId());
        boolean isMember = forumDtoFactory.isMember(post.getForumId(), userId);

        if (!isMember) throw new MemberDoesntBelongForumException("No puedes comentar en este posteo");

        Comment commentToSave = Comment.from(createCommentDto, userId);
        Comment savedComment = commentRepository.save(commentToSave);

        List<Long> usersId = post.getComments().stream().map(Comment::getUserId).distinct().collect(Collectors.toList());
        notificationService.createCommentNotification(userId, post.getUserId(), usersId, post.getId(), savedComment.getId());

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

        if (!isCommentCreator || !isForumOwner) throw new UserNotOwnerException("No tienes permiso para eliminar este comentario");

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

    @Override
    public CommentDto toggleLike(Long id, Long userId) {
        Comment comment = getCommentEntityById(id);
        Comment updatedComment = reactionService.toggleLike(comment, userId);
        Comment savedComment = commentRepository.save(updatedComment);
        return CommentDto.from(savedComment);
    }

    @Override
    public CommentDto toggleDislike(Long id, Long userId) {
        Comment comment = getCommentEntityById(id);
        Comment updatedComment = reactionService.toggleDislike(comment, userId);
        Comment savedComment = commentRepository.save(updatedComment);
        return CommentDto.from(savedComment);
    }
}
