package com.booklink.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDto {

    @Email(message = "El correo ingresado es inválido")
    @NotNull
    private String email;
    @NotBlank
    private String password;

}
