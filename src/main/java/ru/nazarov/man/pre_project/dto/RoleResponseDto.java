package ru.nazarov.man.pre_project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto {
    @NotNull
    @Min(1)
    private Long id;

    @NotBlank
    private String name;
}
