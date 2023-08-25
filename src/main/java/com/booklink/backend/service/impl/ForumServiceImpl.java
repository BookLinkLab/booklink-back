package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.model.Forum;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class ForumServiceImpl implements com.booklink.backend.service.ForumService {
    private final ForumRepository forumRepository;
    private final UserService userService;

    public ForumServiceImpl(ForumRepository forumRepository, UserService userService) {
        this.forumRepository = forumRepository;
        this.userService = userService;
    }

    @Override
    public ForumDto createForum(CreateForumDto forumDto) {
        userService.getUserById(forumDto.getUserId());
        Forum forumToSave = Forum.from(forumDto);
        Forum savedForum = forumRepository.save(forumToSave);
        return ForumDto.from(savedForum);
    }
}
