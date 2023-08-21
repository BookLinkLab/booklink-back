package com.booklink.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    @Email
    @NotNull
    private String email;
    @NotBlank
    private String password;

}
