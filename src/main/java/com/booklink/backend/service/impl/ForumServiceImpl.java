package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.EditForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.exception.JoinOwnForumException;
import com.booklink.backend.exception.MemberAlreadyJoinedForumException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.AlreadyAssignedException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.UserNotAdminException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.User;
import com.booklink.backend.model.Tag;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.service.TagService;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        if (!forum.getUserId().equals(userId))
            throw new UserNotAdminException("Usuario %d no es el administrador del foro %d".formatted(userId, forumId));
        Tag tag = tagService.findOrCreateTag(createTagDto);
        if (forumRepository.existsByIdAndTagsContaining(forumId, tag))
            throw new AlreadyAssignedException("Etiqueta %s ya asignada al foro %d".formatted(tag.getName(), forumId));
        forum.getTags().add(tag);
        Forum savedForum = forumRepository.save(forum);
        return ForumDto.from(savedForum);
    }

    @Override
    public ForumDto editForum(Long forumId, Long userId, EditForumDto editForumDto) {
        Optional<Forum> forumOptional = forumRepository.findById(forumId);
        Forum forumToEdit = forumOptional.orElseThrow(() -> new NotFoundException("Foro %d no encontrado".formatted(forumId)));
        if (!forumToEdit.getUserId().equals(userId))
            throw new UserNotAdminException("Usuario %d no es el administrador del foro %d".formatted(userId, forumId));
        forumToEdit.setName(editForumDto.getName());
        forumToEdit.setDescription(editForumDto.getDescription());
        return ForumDto.from(forumRepository.save(forumToEdit));
    }

    @Override
    public ForumDto joinForum(Long id, Long userId) {
        User memberToJoin = userService.getUserEntityById(userId);
        Forum forumToJoin = getForumEntityById(id);

        if (forumToJoin.getUser().getId().equals(userId))
            throw new JoinOwnForumException("No puedes unirte a tu propio foro");

        forumToJoin.getMembers()
                .stream()
                .filter(member -> member.getId().equals(memberToJoin.getId()))
                .findAny()
                .ifPresent(existingMember -> {
                    throw new MemberAlreadyJoinedForumException("Ya perteneces a este foro");
                });

        forumToJoin.getMembers().add(memberToJoin);
        forumRepository.save(forumToJoin);

        return ForumDto.from(forumToJoin);
    }

    @Override
    public Forum getForumEntityById(Long id) {
        Optional<Forum> forumOptional = forumRepository.findById(id);
        return forumOptional.orElseThrow(() -> new NotFoundException("Forum %s not found".formatted(id)));
    }
}
