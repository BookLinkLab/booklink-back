package com.booklink.backend.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostDto {
    @NotNull(message = "El id del foro no puede estar vacío")
    private Long forumId;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 512)
    private String content;

}
