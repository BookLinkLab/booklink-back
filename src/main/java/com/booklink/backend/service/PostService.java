package com.booklink.backend.service;

import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;

public interface PostService {
    PostDto createPost(CreatePostDto createPostDto, Long userId);
}
