package com.booklink.backend.dto.post;


import com.booklink.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPostDto {
    private Long id;
    private String username;

    public static UserPostDto from(User userPostDto) {
        return UserPostDto.builder()
                .id(userPostDto.getId())
                .username(userPostDto.getUsername())
                .build();
    }
}
