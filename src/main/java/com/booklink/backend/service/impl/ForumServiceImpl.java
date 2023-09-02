package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.exception.AlreadyAssignedException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotAdminException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.service.TagService;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumServiceImpl implements com.booklink.backend.service.ForumService {
    private final ForumRepository forumRepository;
    private final UserService userService;
    private final TagService tagService;

    public ForumServiceImpl(ForumRepository forumRepository, UserService userService, TagService tagService) {
        this.forumRepository = forumRepository;
        this.userService = userService;
        this.tagService = tagService;
    }

    @Override
    public ForumDto createForum(CreateForumDto forumDto, Long userId) {
        UserProfileDto forumCreator = userService.getUserById(userId);
        Forum forumToSave = Forum.from(forumDto, forumCreator.getId());
        Forum savedForum = forumRepository.save(forumToSave);
        return ForumDto.from(savedForum);
    }

    @Override
    public List<ForumDto> getAllForums() {
        List<Forum> forums = forumRepository.findAll();
        return forums.stream().map(ForumDto::from).toList();
    }

    @Override
    public ForumDto addTagToForum(Long forumId, Long userId, CreateTagDto createTagDto) {
        Forum forum = forumRepository.findById(forumId).orElseThrow(() -> new NotFoundException("Foro %d no encontrado".formatted(forumId)));
        if (!forum.getUserId().equals(userId)) throw new UserNotAdminException("Usuario %d no es el administrador del foro %d".formatted(userId, forumId));
        Tag tag = tagService.findOrCreateTag(createTagDto);
        if (forum.getTags().contains(tag)) throw new AlreadyAssignedException("Etiqueta %s ya asignada al foro %d".formatted(tag.getName(), forumId));
        Forum savedForum = forumRepository.save(forum);
        return ForumDto.from(savedForum);
    }
}
