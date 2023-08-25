package com.booklink.backend.dto.forum;

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
    private List<UserDto> memberList;

    public static ForumDto from(Forum forum) {
        return ForumDto.builder()
                .id(forum.getId())
                .name(forum.getName())
                .userId(forum.getUser_id())
                .description(forum.getDescription())
                .img(forum.getImg())
                .memberList(forum.getMember_list().stream().map(UserDto::from).toList())
                .build();
    }
}
