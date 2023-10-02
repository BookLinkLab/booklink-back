package com.booklink.backend.controller;

import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.EditPostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.service.PostService;
import com.booklink.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(createPostDto);
        PostDto postDto = postService.createPost(createPostDto, securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }
    @GetMapping("/forum/{id}")
    public ResponseEntity<?> getPostsByForumId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByForumId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostViewById(securityUtil.getLoggedUserId(), id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id, @Valid @RequestBody EditPostDto editPostDto) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.editPost(id, editPostDto, securityUtil.getLoggedUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id,securityUtil.getLoggedUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Post deleted");
    }

}
