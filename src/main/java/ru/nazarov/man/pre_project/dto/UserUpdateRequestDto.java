package ru.nazarov.man.pre_project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    @NotNull
    @Min(1)
    private Long id;

    private String password;

    private String confirmPassword;

    @NotNull
    @NotEmpty(message = "Must be at least one role")
    private Set<Long> roles;
}