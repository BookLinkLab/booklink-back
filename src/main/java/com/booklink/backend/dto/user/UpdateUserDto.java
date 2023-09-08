package com.booklink.backend.dto.user;

import jakarta.validation.constraints.Email;
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
public class UpdateUserDto {
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "El nombre de usuario solo debe tener letras y números")
    @Size(min = 3, max = 24, message = "El nombre de usuario debe tener entre 3 y 24 caracteres")
    private String username;

    @Email
    private String email;

    private String password;
}
