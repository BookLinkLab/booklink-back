package com.booklink.backend.dto.post;

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
public class EditPostDto {

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 512)
    private String content;

}
