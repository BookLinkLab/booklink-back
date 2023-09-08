package com.booklink.backend.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDto {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 24, message = "El nombre de usuario debe tener entre 3 y 24 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "El nombre de usuario solo debe contener letras y números")
    private String username;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe seguir el formato correcto")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 24, message = "La contraseña debe tener entre 8 y 24 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "La contraseña solo debe contener letras y números")
    private String password;
}
