package com.booklink.backend.dto.forum;

import com.booklink.backend.model.Forum;
import com.booklink.backend.service.ForumService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;

@Service
@Lazy
public class ForumDtoFactory {
    private final ForumService forumService;

    public ForumDtoFactory(ForumService forumService) {
        this.forumService = forumService;
    }

    public <T> List<T> createForumDtoAndForumViewDtoWithIsMember(List<Forum> forums, Long userId, BiFunction<Forum, Boolean, T> builder) {
        List<Forum> forumsCreated = forumService.getForumsCreated(userId);
        List<Forum> forumsJoined = forumService.getForumsJoined(userId);
        return forums.stream()
                .map(forum -> builder.apply(forum, isMember(forum.getId(), forumsCreated, forumsJoined)))
                .toList();
    }

    public boolean isMember(Long forumId, Long userId) {
        List<Forum> forumsJoined = forumService.getForumsJoined(userId);
        List<Forum> forumsCreated = forumService.getForumsCreated(userId);
        return isMember(forumId, forumsCreated, forumsJoined);
    }

    public boolean isForumOwner(Long forumId, Long userId) {
        Forum forum = forumService.getForumEntityById(forumId);
        return forum.getUserId().equals(userId);
    }

    private boolean isMember(Long forumId, List<Forum> forumsCreated, List<Forum> forumsJoined) {
        boolean isMemberOfJoined = forumsJoined.stream()
                .anyMatch(joinedForum -> joinedForum.getId().equals(forumId));

        boolean isMemberOfCreated = forumsCreated.stream()
                .anyMatch(createdForum -> createdForum.getId().equals(forumId));

        return isMemberOfJoined || isMemberOfCreated;
    }

    public ForumDto getForumDtoById(Long forumId) {
        Forum forum = forumService.getForumEntityById(forumId);
        return ForumDto.builder()
                .id(forum.getId())
                .name(forum.getName())
                .description(forum.getDescription())
                .img(forum.getImg())
                .build();

    }
}
