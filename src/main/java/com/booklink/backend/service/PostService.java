package com.booklink.backend.service;

import com.booklink.backend.dto.post.*;
import com.booklink.backend.model.Post;

import java.util.List;

public interface PostService {
    PostDto createPost(CreatePostDto createPostDto, Long userId);
    List<PostInfoDto> getPostsByForumId(Long forumId);
    PostDto editPost(Long postId, EditPostDto editPostDto, Long userId);
    PostDto getPostById(Long id);
    Post getPostEntity(Long id);
    PostViewDto getPostViewById(Long userId, Long postId);
    void deletePost(Long id, Long userId);
    PostDto toggleLike(Long id, Long userId);
    PostDto toggleDislike(Long id, Long userId);
    List<PostPreviewDto> getLatestPostsByUserId(Long id);
}
