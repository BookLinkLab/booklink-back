package com.booklink.backend.service;

import com.booklink.backend.dto.forum.*;
import com.booklink.backend.dto.post.PostInfoDto;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.model.Forum;

import java.util.List;

public interface ForumService {

    ForumDto createForum(CreateForumDto forumDto, Long userId);

    List<ForumDto> getAllForums();

    ForumDto joinForum(Long id, Long userId);

    Forum getForumEntityById(Long id);

    ForumDto addTagToForum(Long forumId, Long userId, CreateTagDto createTagDto);

    ForumDto editForum(Long id, Long userId, EditForumDto editForumDto);

    List<ForumViewDto> searchForums(String forumName, Long userId);

    ForumGetDto getForumById(Long id, Long userId);

    void deleteForum(Long id, Long userId);

    void leaveForum(Long id, Long userId);

}
