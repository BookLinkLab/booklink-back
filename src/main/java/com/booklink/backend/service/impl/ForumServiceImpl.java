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
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

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
    public ForumDto createForum(CreateForumDto createForumDto, Long userId) {
        User forumCreator = userService.getUserEntityById(userId);
        validateImage(createForumDto.getImg());
        Forum forumToSave = Forum.from(createForumDto, forumCreator.getId());

        List<CreateTagDto> createTagDto = createForumDto.getTags();
        List<Tag> tags = createTagDto.stream().map(tagService::findOrCreateTag).toList();

        forumToSave.setTags(tags);

        Forum savedForum = forumRepository.save(forumToSave);
        return ForumDto.from(savedForum, true);
    }

    @Override
    public List<ForumDto> getAllForums() {
        List<Forum> forums = forumRepository.findAll();
        return forums.stream().map(forum -> ForumDto.from(forum, false)).toList();
    }

    @Override
    public ForumDto addTagToForum(Long forumId, Long userId, CreateTagDto createTagDto) {
        Forum forum = getForumEntityById(forumId);
        if (!forum.getUserId().equals(userId))
            throw new UserNotAdminException("El usuario no es el administrador del foro %s".formatted(forum.getName()));
        Tag tag = tagService.findOrCreateTag(createTagDto);
        if (forumRepository.existsByIdAndTagsContaining(forumId, tag))
            throw new AlreadyAssignedException("La etiqueta %s ya fue asignada al foro %s".formatted(tag.getName(), forum.getName()));
        forum.getTags().add(tag);
        Forum savedForum = forumRepository.save(forum);
        return ForumDto.from(savedForum, true);
    }

    @Override
    public ForumDto editForum(Long forumId, Long userId, EditForumDto editForumDto) {
        Forum forumToEdit = getForumEntityById(forumId);
        if (!forumToEdit.getUserId().equals(userId))
            throw new UserNotAdminException("El usuario no es el administrador del foro %s".formatted(forumToEdit.getName()));
        if (editForumDto.getName() != null) forumToEdit.setName(editForumDto.getName());
        if (editForumDto.getDescription() != null) forumToEdit.setDescription(editForumDto.getDescription());
        List<Tag> oldTags = new ArrayList<>(forumToEdit.getTags());
        if (editForumDto.getTags() != null) {
            List<Tag> newTags = editForumDto.getTags().stream().map(tagService::findOrCreateTag).toList();
            forumToEdit.getTags().clear();
            forumToEdit.getTags().addAll(newTags);
        }
        if (editForumDto.getImg() != null) {
            validateImage(editForumDto.getImg());
            forumToEdit.setImg(editForumDto.getImg());
        }
        Forum savedForum = forumRepository.save(forumToEdit);
        for (Tag tag : oldTags) {
            if (!forumRepository.existsByTagsContaining(tag)) {
                tagService.deleteTag(tag.getId());
            }
        }
        return ForumDto.from(savedForum,true);
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
        int newMembersAmount = forumToJoin.getMembersAmount() + 1;
        forumToJoin.setMembersAmount(newMembersAmount);
        forumRepository.save(forumToJoin);

        return ForumDto.from(forumToJoin,true);
    }

    @Override
    public Forum getForumEntityById(Long id) {
        Optional<Forum> forumOptional = forumRepository.findById(id);
        return forumOptional.orElseThrow(() -> new NotFoundException("El foro no fue encontrado"));
    }

    @Override
    public List<ForumViewDto> searchForums(String searchTerm, Long userId) {
        if (searchTerm != null && !searchTerm.isBlank()) {
            List<String> words = Arrays.stream(searchTerm.split("\\s+"))
                    .filter(s -> !s.trim().isEmpty())
                    .toList();
            List<Forum> nameSearchResults = forumRepository.findAllByNameContainingIgnoreCase(searchTerm);
            List<Forum> tagSearchResults = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                if(i == 0) {
                    tagSearchResults.addAll(forumRepository.findAllByTagsNameIsIgnoreCase(word));
                    continue;
                }
                List<Forum> forumsToKeep = new ArrayList<>();
                List<Forum> newForumsToAdd = forumRepository.findAllByTagsNameIsIgnoreCase(word);
                tagSearchResults.forEach(forum -> {
                    if(newForumsToAdd.stream().anyMatch(newForum -> newForum.getId().equals(forum.getId())))
                        forumsToKeep.add(forum);
                });
                tagSearchResults = forumsToKeep;
            }
            nameSearchResults.addAll(tagSearchResults);
            // Remove duplicates
            List<Forum> result = nameSearchResults.stream()
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(Forum::getId))),
                            ArrayList::new));
            return ForumDtoFactory.createForumDtoAndForumViewDtoWithIsMember(result, userId, ForumViewDto::from);
        } else {
            List<Forum> forums = forumRepository.findAll();
            return ForumDtoFactory.createForumDtoAndForumViewDtoWithIsMember(forums, userId, ForumViewDto::from);
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
    public ForumGetDto getForumById(Long id, Long userId) {
        Forum forum = this.getForumEntityById(id);
        boolean isMember = ForumDtoFactory.isMember(forum, userId);
        return ForumGetDto.from(forum, isMember);
    }


    @Override
    public void leaveForum(Long id, Long userId) {
        User memberToLeave = userService.getUserEntityById(userId);
        Forum forumToLeave = getForumEntityById(id);

        boolean removed = forumToLeave.getMembers().removeIf(member -> member.getId().equals(memberToLeave.getId()));
        if (removed) {
            int newMembersAmount = forumToLeave.getMembersAmount() - 1;
            forumToLeave.setMembersAmount(newMembersAmount);
            forumRepository.save(forumToLeave);}
        else
            throw new MemberDoesntBelongForumException("No perteneces al foro %s".formatted(forumToLeave.getName()));
    }

    private void validateImage(String img){
        if (img != null) {
            try {
                BufferedImage image = ImageIO.read(new URL(img));
                if (image == null) throw new IOException();
            } catch (IOException e) {
                throw new InvalidImageException("La imagen no es válida");
            }
        }
    }
}
