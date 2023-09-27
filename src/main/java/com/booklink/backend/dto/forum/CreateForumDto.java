package com.booklink.backend.dto.forum;

import com.booklink.backend.dto.tag.CreateTagDto;
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
    @NotBlank(message = "El nombre del foro no puede estar vacío")
    @Size(min = 4, max = 32, message = "El nombre del foro debe tener entre 4 y 32 caracteres")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 512, message = "La descripción no puede tener más de 512 caracteres")
    private String description;

    private String img;

    @NotNull
    private List<CreateTagDto> tags;
}
