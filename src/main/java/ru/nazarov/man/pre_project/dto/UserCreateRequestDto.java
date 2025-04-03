package ru.nazarov.man.pre_project.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.nazarov.man.pre_project.validators.FieldsValueMatch;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "password",
                fieldMatch = "confirmPassword",
                message = "Passwords do not match!"
        )
})
public class UserCreateRequestDto {

    @NotBlank(message = "The Username cannot be empty")
    @Size(min = 2, max = 20, message = "The Username must be between 2 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "The Username must contain only Latin letters, numbers and characters ._-"
    )
    private String username;

    @NotBlank(message = "The Password cannot be empty")
    @Size(min = 4, max = 100, message = "The Password must be between 4 and 100 characters")
/*
    @Pattern(
            regexp = "^(?=.*[0-9])" +
                    "(?=.*[!@#$%^&*_+-])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    ".{4,}$",
            message = "The Password must contain at least "
                    + "one lowercase letter, "
                    + "one uppercase letter, "
                    + "one number and "
                    + "one special character !@#$%^&*_-"
    )
*/
    private String password;

    private String confirmPassword;

    @NotEmpty(message = "Must be at least one role")
    private Set<Long> roles = new HashSet<>();
}
