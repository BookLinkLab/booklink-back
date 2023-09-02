package com.booklink.backend.service;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.model.Forum;

import java.util.List;

public interface ForumService {

    ForumDto createForum(CreateForumDto forumDto, Long userId);

    List<ForumDto> getAllForums();

    ForumDto joinForum(Long id, Long userId);

    Forum getForumEntityById(Long id);
}
