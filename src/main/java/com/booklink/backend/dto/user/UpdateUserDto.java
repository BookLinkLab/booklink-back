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
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Username must contain only letters and numbers")
    @Size(min = 3, max = 24, message = "Username must have between 3 and 24 characters.")
    private String username;

    @Email
    private String email;

    private String password;
}