package com.booklink.backend.dto.post;

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
public class EditPostDto {

    @Size(max = 512,message = "La publicacion no puede tener más de 512 caracteres")
    private String content;

}
