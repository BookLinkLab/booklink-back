package com.booklink.backend.dto.forum;

import com.booklink.backend.model.Forum;
import com.booklink.backend.service.ForumService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<T> forumsDto = new ArrayList<>();
        for (Forum forum : forums) {
            boolean isMember = isMember(forum.getId(), userId);
            forumsDto.add(builder.apply(forum, isMember));
        }
        return forumsDto;
    }

    public boolean isMember(Long forumId, Long userId) {
        List<Forum> forumsJoined = forumService.getForumsJoined(userId);
        List<Forum> forumsCreated = forumService.getForumsCreated(userId);

        boolean isMemberOfJoined = forumsJoined.stream()
                .anyMatch(joinedForum -> joinedForum.getId().equals(forumId));

        boolean isMemberOfCreated = forumsCreated.stream()
                .anyMatch(createdForum -> createdForum.getId().equals(forumId));

        return isMemberOfJoined || isMemberOfCreated;
    }
}
