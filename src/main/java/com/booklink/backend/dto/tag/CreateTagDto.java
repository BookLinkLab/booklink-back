package com.booklink.backend.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 3, max = 16, message = "El nombre de la etiqueta debe tener entre 3 y 16 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9.]+$", message = "El nombre de la etiqueta solo debe tener letras, números y puntos")
    @NotBlank(message = "El nombre de la etiqueta no puede estar vacío")
    private String name;
}
