package com.booklink.backend.dto.tag;

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
public class CreateTagDto {
    @NotBlank(message = "Nombre de la etiqueta no puede estar vacío")
    @Size(min = 3, max = 20, message = "Nombre de la etiqueta debe tener entre 4 y 32 caracteres")
    private String name;
}