package com.booklink.backend.dto.user;

import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {

    private Long id;
    private String username;
    private String email;
    private List<ForumDto> forumsCreated;
    private List<ForumDto> forumsJoined;

    public static UserProfileDto from(User user, List<ForumDto> forumCreated, List<ForumDto> forumJoined) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .forumsCreated(forumCreated)
                .forumsJoined(forumJoined)
                .build();
    }
}
