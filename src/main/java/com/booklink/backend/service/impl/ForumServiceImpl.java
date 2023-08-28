package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.model.Forum;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumServiceImpl implements com.booklink.backend.service.ForumService {
    private final ForumRepository forumRepository;
    private final UserService userService;

    public ForumServiceImpl(ForumRepository forumRepository, UserService userService) {
        this.forumRepository = forumRepository;
        this.userService = userService;
    }

    @Override
    public ForumDto createForum(CreateForumDto forumDto, String username) {
        UserDto forumCreator = userService.getUserByUsername(username);
        Forum forumToSave = Forum.from(forumDto, forumCreator.getId());
        Forum savedForum = forumRepository.save(forumToSave);
        return ForumDto.from(savedForum);
    }

    @Override
    public List<ForumDto> getAllForums() {
        List<Forum> forums = forumRepository.findAll();
        return forums.stream().map(ForumDto::from).toList();
    }
}
