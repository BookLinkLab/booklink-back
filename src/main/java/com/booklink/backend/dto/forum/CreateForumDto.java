package com.booklink.backend.dto.forum;

import com.booklink.backend.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateForumDto {
    @NotBlank(message = "Username must not be blank")
    @Size(min = 4, max = 32, message = "Forum name must be between 4 and 32 characters")
    private String name;

    @NotNull(message = "Forum creator must not be blank")
    private Long userId;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotBlank(message = "Image url must not be blank")
    private String img;

    private List<UserDto> members;
}
