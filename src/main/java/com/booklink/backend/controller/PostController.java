package com.booklink.backend.controller;

import com.booklink.backend.dto.post.*;
import com.booklink.backend.service.PostService;
import com.booklink.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final SecurityUtil securityUtil;

    public PostController(PostService postService, SecurityUtil securityUtil) {
        this.postService = postService;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostDto createPostDto) {
        PostDto postDto = postService.createPost(createPostDto, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }

    @GetMapping("/forum/{id}")
    public ResponseEntity<List<PostInfoDto>> getPostsByForumId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByForumId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostViewDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostViewById(securityUtil.getLoggedUserId(), id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> editPost(@PathVariable Long id, @Valid @RequestBody EditPostDto editPostDto) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.editPost(id, editPostDto, securityUtil.getLoggedUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("La publicación fue eliminada con éxito");
    }

    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<PostDto> toggleLike(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.toggleLike(id, securityUtil.getLoggedUserId()));
    }

    @PostMapping("/{id}/toggle-dislike")
    public ResponseEntity<PostDto> toggleDislike(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.toggleDislike(id, securityUtil.getLoggedUserId()));
    }
}
