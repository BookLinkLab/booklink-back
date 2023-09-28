package com.booklink.backend.dto.post;

import com.booklink.backend.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInfoDto {
    UserPostDto user;
    String content;
    Date date;

    public static PostInfoDto from(Post postInfoDto) {
        return PostInfoDto.builder()
                .user(UserPostDto.from(postInfoDto.getUser()))
                .content(postInfoDto.getContent())
                .date(postInfoDto.getCreatedDate())
                .build();
    }
}
