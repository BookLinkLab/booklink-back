package com.booklink.backend.dto.forum;

import com.booklink.backend.dto.tag.TagDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.model.Forum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumDto {
    private Long id;
    private String name;
    private Long userId;
    private String description;
    private String img;
    private List<UserDto> members;
    private List<TagDto> tags;
    private boolean searcherIsMember;

    public static ForumDto from(Forum forum, boolean isForumMember) {
        return ForumDto.builder()
                .id(forum.getId())
                .name(forum.getName())
                .userId(forum.getUserId())
                .description(forum.getDescription())
                .img(forum.getImg())
                .members(forum.getMembers().stream().map(UserDto::from).toList())
                .tags(forum.getTags().stream().map(TagDto::from).toList())
                .searcherIsMember(isForumMember)
                .build();
    }
}
