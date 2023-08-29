package com.booklink.backend.service;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ForumService {

    ForumDto createForum(CreateForumDto forumDto, String username);

    List<ForumDto> getAllForums();
}
