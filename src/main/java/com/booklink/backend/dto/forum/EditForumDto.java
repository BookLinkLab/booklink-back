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
public class EditForumDto {
    @NotBlank(message = "El nombre del foro no puede estar vacío")
    @Size(min = 4, max = 32, message = "El nombre del foro debe tener entre 4 y 32 caracteres")
    private String name;

    @NotBlank(message = "La descripción no debe estar vacía")
    @Size(max = 512)
    private String description;

}
