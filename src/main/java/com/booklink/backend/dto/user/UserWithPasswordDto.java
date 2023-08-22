package com.booklink.backend.dto.user;

import com.booklink.backend.model.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithPasswordDto {
    private Long id;
    private String username;
    private String email;
    private String password;

    public static UserWithPasswordDto from(@NonNull User user) {
        return UserWithPasswordDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
