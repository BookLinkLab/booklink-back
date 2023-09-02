package com.booklink.backend.service.impl;

import com.booklink.backend.dto.forum.CreateForumDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.exception.MemberAlreadyJoinedForumException;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ForumServiceImpl implements com.booklink.backend.service.ForumService {
    private final ForumRepository forumRepository;
    private final UserService userService;

    public ForumServiceImpl(ForumRepository forumRepository, UserService userService) {
        this.forumRepository = forumRepository;
        this.userService = userService;
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
    public ForumDto joinForum(Long id, Long userId) {
        User memberToJoin = userService.getUserEntityById(userId);
        Forum forumToJoin = getForumEntityById(id);

        forumToJoin.getMembers()
                .stream()
                .filter(member -> member.getId().equals(memberToJoin.getId()))
                .findAny()
                .ifPresent(existingMember -> {
                    throw new MemberAlreadyJoinedForumException("El usuario %s ya se unió al foro".formatted(memberToJoin.getId()));
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
