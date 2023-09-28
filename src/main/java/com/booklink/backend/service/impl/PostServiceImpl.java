package com.booklink.backend.service.impl;


import com.booklink.backend.dto.post.CreatePostDto;
import com.booklink.backend.dto.post.PostDto;
import com.booklink.backend.dto.post.PostInfoDto;
import com.booklink.backend.exception.UserNotMemberException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Post;
import com.booklink.backend.model.User;
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
        Forum forum = forumService.getForumEntityById(createPostDto.getForumId());
        for (User user: forum.getMembers()) {
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
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id).orElseThrow();
        Forum forum = forumService.getForumEntityById(post.getForum().getId());
        if (post.getUser().getId().equals(userId) || forum.getUser().getId().equals(userId)) {
            postRepository.deleteById(id);
        } else {
            throw new UserNotMemberException("No sos miembro de este foro");
        }
    }


}
