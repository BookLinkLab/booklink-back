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

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 24, message = "Username must be between 3 and 24 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must contain only letters and numbers")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Password must contain only letters and numbers")
    private String password;
}
