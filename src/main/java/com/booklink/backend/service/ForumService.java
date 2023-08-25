package com.booklink.backend.service;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;

public interface ForumService {

    ForumDto createForum(CreateForumDto forumDto);
}
