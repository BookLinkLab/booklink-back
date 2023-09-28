package com.booklink.backend.service;

import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.post.PostInfoDto;

import java.util.List;

public interface PostService {
    PostDto createPost(CreatePostDto createPostDto, Long userId);
    List<PostInfoDto> getPostsByForumId(Long forumId);

    void deletePost(Long id, Long userId);

}
