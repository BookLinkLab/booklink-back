package com.booklink.backend.service;

import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.EditPostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.post.PostInfoDto;
import com.booklink.backend.dto.post.PostViewDto;
import com.booklink.backend.model.Post;

import java.util.List;

public interface PostService {
    PostDto createPost(CreatePostDto createPostDto, Long userId);

    List<PostInfoDto> getPostsByForumId(Long forumId);
    PostDto editPost(Long postId, EditPostDto editPostDto, Long userId);

    PostDto getPostById(Long id);
    Post getPostEntity(Long id);
    PostViewDto getPostViewById(Long id);
}
