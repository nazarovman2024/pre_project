package ru.nazarov.man.pre_project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {

    @NotBlank(message = "The Username cannot be empty")
    private String username;

    @NotBlank(message = "The Password cannot be empty")
    private String password;

    private String confirmPassword;

    @NotNull
    @NotEmpty(message = "Must be at least one role")
    private Set<Long> roles = new HashSet<>();
}