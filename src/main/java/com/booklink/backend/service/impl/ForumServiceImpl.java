package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.*;
import com.booklink.backend.dto.tag.CreateTagDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.exception.*;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.service.TagService;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        Forum forum = forumRepository.findById(forumId).orElseThrow(() -> new NotFoundException("Foro %s no encontrado".formatted(getForumById(forumId).getTitle())));
        if (!forum.getUserId().equals(userId))
            throw new UserNotAdminException("El usuario no es el administrador del foro %s".formatted(getForumById(forumId).getTitle()));
        Tag tag = tagService.findOrCreateTag(createTagDto);
        if (forumRepository.existsByIdAndTagsContaining(forumId, tag))
            throw new AlreadyAssignedException("La etiqueta %s ya fue asignada al foro %s".formatted(tag.getName(), getForumById(forumId).getTitle()));
        forum.getTags().add(tag);
        Forum savedForum = forumRepository.save(forum);
        return ForumDto.from(savedForum);
    }

    @Override
    public ForumDto editForum(Long forumId, Long userId, EditForumDto editForumDto) {
        Optional<Forum> forumOptional = forumRepository.findById(forumId);
        Forum forumToEdit = forumOptional.orElseThrow(() -> new NotFoundException("Foro %s no encontrado".formatted(getForumById(forumId).getTitle())));
        if (!forumToEdit.getUserId().equals(userId))
            throw new UserNotAdminException("El usuario no es el administrador del foro %s".formatted(getForumById(forumId).getTitle()));
        forumToEdit.setName(editForumDto.getName());
        forumToEdit.setDescription(editForumDto.getDescription());
        List<Tag> oldTags = new ArrayList<>(forumToEdit.getTags());
        List<Tag> newTags = editForumDto.getTags().stream().map(tagService::findOrCreateTag).toList();
        forumToEdit.getTags().clear();
        forumToEdit.getTags().addAll(newTags);
        Forum savedForum = forumRepository.save(forumToEdit);
        for (Tag tag: oldTags) {
            if (!forumRepository.existsByTagsContaining(tag)) {
                tagService.deleteTag(tag.getId());
            }
        }
        return ForumDto.from(savedForum);
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
        return forumOptional.orElseThrow(() -> new NotFoundException("El foro %s no fue encontrado".formatted(id)));
    }

    @Override
    public List<ForumViewDto> searchForums(String forumName, List<Long> tagIds) {
        if (tagIds == null && forumName != null) {
            List<Forum> forums = forumRepository.findAllByNameContainingIgnoreCase(forumName);
            return forums.stream().map(ForumViewDto::from).toList();
        } else if (forumName == null && tagIds != null) {
            List<Forum> forums = forumRepository.findAllByTagsIdIn(tagIds);
            return forums.stream().map(ForumViewDto::from).toList();
        } else if (forumName == null) {
            List<Forum> forums = forumRepository.findAll();
            return forums.stream().map(ForumViewDto::from).toList();
        } else {
            List<Forum> forums = forumRepository.findAllByNameContainingIgnoreCaseAndTagsIdIsIn(forumName, tagIds);
            return forums.stream().map(ForumViewDto::from).toList();
        }
    }

    @Override
    public void deleteForum(Long id, Long userId) {
        Forum forumToDelete = getForumEntityById(id);
        List<Tag> tagToDelete = forumToDelete.getTags();
        if (!forumToDelete.getUserId().equals(userId))
            throw new UserNotAdminException("Solo el administrador puede eliminar sus foros");
        forumRepository.delete(forumToDelete);
        for (Tag tag : tagToDelete) {
            if (!forumRepository.existsByTagsContaining(tag)) {
                tagService.deleteTag(tag.getId());
            }
        }
    }
    @Override
    public ForumGetDto getForumById(Long id) {
        Forum forum = this.getForumEntityById(id);
        return ForumGetDto.from(forum);
    }


    @Override
    public void leaveForum(Long id, Long userId) {
        User memberToLeave = userService.getUserEntityById(userId);
        Forum forumToLeave = getForumEntityById(id);

        boolean removed = forumToLeave.getMembers().removeIf(member -> member.getId().equals(memberToLeave.getId()));
        if (removed) forumRepository.save(forumToLeave);
        else
            throw new MemberDoesntBelongForumException("No perteneces al foro %s".formatted(getForumById(id).getTitle()));
    }
}
