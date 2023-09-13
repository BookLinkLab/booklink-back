package com.booklink.backend.dto.forum;

import com.booklink.backend.dto.tag.CreateTagDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class EditForumDto {
    @NotBlank(message = "El nombre del foro no puede ser vacío")
    @Size(min = 4, max = 32, message = "El nombre del foro debe tener entre 4 y 32 caracteres")
    private String name;

    @NotBlank(message = "La descripción no debe estar vacía")
    @Size(max = 512)
    private String description;

    @NotEmpty(message = "La lista de etiquetas no puede estar vacía")
    private List<CreateTagDto> tags;

}
