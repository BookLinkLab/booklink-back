package com.booklink.backend.dto.forum;

import com.booklink.backend.model.Forum;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ForumDtoFactory {

    public static <T> List<T> createForumViewDtoWithIsMember(List<Forum> forums, Long userId, BiFunction<Forum, Boolean, T> builder) {
        List<T> forumDtos = new ArrayList<>();
        for (Forum forum : forums) {
            boolean isMember = isMember(forum, userId);
            forumDtos.add(builder.apply(forum, isMember));
        }
        return forumDtos;
    }

    public static boolean isMember(Forum forum, Long userId) {
        return forum.getMembers().stream().anyMatch(member -> member.getId().equals(userId)) || forum.getUserId().equals(userId);
    }


}
