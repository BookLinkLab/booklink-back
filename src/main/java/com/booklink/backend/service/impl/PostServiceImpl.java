package com.booklink.backend.service.impl;


import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.post.PostInfoDto;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Post;
import com.booklink.backend.repository.PostRepository;
import com.booklink.backend.service.ForumService;
import com.booklink.backend.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    private final ForumService forumService;

    public PostServiceImpl(PostRepository postRepository, ForumService forumService) {
        this.postRepository = postRepository;
        this.forumService = forumService;
    }

    @Override
    public PostDto createPost(CreatePostDto createPostDto, Long userId) {
        Post post = Post.from(createPostDto, userId);
        forumService.getForumEntityById(createPostDto.getForumId());
        Post savedPost = postRepository.save(post);
        return PostDto.from(savedPost);
    }

    @Override
    public List<PostInfoDto> getPostsByForumId(Long forumId) {
        List<Post> posts = postRepository.findAllByForumId(forumId);
        return posts.stream().map(PostInfoDto::from).toList();
    }


}
