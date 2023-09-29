package com.booklink.backend.dto.comment;

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
public class CreateCommentDto {
    @NotNull(message = "El id del post no puede estar vacío")
    private Long postId;
    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 512, message = "El comentario no debe superar los 512 caracteres")
    private String content;
}
