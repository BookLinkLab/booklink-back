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
public class ForumGetDto {
    private Long id;
    private String title;
    private String description;
    private String owner;
    private String img;
    private Integer members;
    private List<TagDto> tags;

    public static ForumGetDto from(Forum forum) {
        return ForumGetDto.builder()
                .id(forum.getId())
                .title(forum.getName())
                .description(forum.getDescription())
                .owner(forum.getUser().getUsername())
                .img(forum.getImg())
                .members(forum.getMembers().size())
                .tags(forum.getTags().stream().map(TagDto::from).toList())
                .build();
    }
}
