package com.booklink.backend.service.impl;


import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.post.PostInfoDto;
import com.booklink.backend.dto.post.PostViewDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotMemberException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Post;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.PostRepository;
import com.booklink.backend.service.ForumService;
import com.booklink.backend.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
        Forum forum = forumService.getForumEntityById(createPostDto.getForumId());
        for (User user : forum.getMembers()) {
            if (user.getId().equals(userId) || forum.getUser().getId().equals(userId)) {
                Post savedPost = postRepository.save(post);
                return PostDto.from(savedPost);
            }
        }
        throw new UserNotMemberException("No sos miembro de este foro");
    }

    @Override
    public List<PostInfoDto> getPostsByForumId(Long forumId) {
        forumService.getForumEntityById(forumId);
        List<Post> posts = postRepository.findAllByForumId(forumId);
        return posts.stream().map(PostInfoDto::from).toList();
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = getPostEntity(id);
        return PostDto.from(post);
    }

    @Override
    public Post getPostEntity(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElseThrow(() -> new NotFoundException("El posteo no fue encontrado"));
    }

    @Override
    public PostViewDto getPostViewById(Long id) {
        Post post = getPostEntity(id);
        return PostViewDto.from(post);
    }
}
