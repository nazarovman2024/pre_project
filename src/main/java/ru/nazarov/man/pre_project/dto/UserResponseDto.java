package ru.nazarov.man.pre_project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    @NotBlank
    @Min(1)
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    @NotEmpty
    private Set<Long> roles;
}
