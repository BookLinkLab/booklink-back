package com.booklink.backend.dto.forum;

import com.booklink.backend.dto.tag.TagDto;
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
public class ForumViewDto {
    private Long id;
    private String name;
    private String img;
    private Integer members;
    private List<TagDto> tags;
    private boolean searcherIsMember;

    public static ForumViewDto from(Forum forum, boolean isForumMember) {
        return ForumViewDto.builder()
                .id(forum.getId())
                .name(forum.getName())
                .img(forum.getImg())
                .members(forum.getMembersAmount())
                .tags(forum.getTags().stream().map(TagDto::from).toList())
                .searcherIsMember(isForumMember)
                .build();
    }
}
