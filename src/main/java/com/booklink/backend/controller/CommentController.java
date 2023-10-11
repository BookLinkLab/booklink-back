package com.booklink.backend.controller;

import com.booklink.backend.dto.comment.CommentDto;
import com.booklink.backend.dto.comment.CreateCommentDto;
import com.booklink.backend.dto.comment.EditCommentDto;
import com.booklink.backend.service.CommentService;
import com.booklink.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final SecurityUtil securityUtil;

    public CommentController(CommentService commentService, SecurityUtil securityUtil) {
        this.commentService = commentService;
        this.securityUtil = securityUtil;
    }

    @PostMapping()
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CreateCommentDto createCommentDto) {
        CommentDto postDto = commentService.createComment(createCommentDto, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Comentario eliminado correctamente");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editComment(@PathVariable Long id, @Valid @RequestBody EditCommentDto editCommentDto) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.editComment(id, editCommentDto, securityUtil.getLoggedUserId()));
    }

    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.toggleLike(id, securityUtil.getLoggedUserId()));
    }

    @PostMapping("/{id}/toggle-dislike")
    public ResponseEntity<?> toggleDislike(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.toggleDislike(id, securityUtil.getLoggedUserId()));
    }
}
