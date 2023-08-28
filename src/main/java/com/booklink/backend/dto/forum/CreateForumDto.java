package com.booklink.backend.dto.forum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateForumDto {
    @NotBlank(message = "Forum name must not be blank")
    @Size(min = 4, max = 32, message = "Forum name must be between 4 and 32 characters")
    private String name;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 512)
    private String description;

    private String img;
}
