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
public class UserUpdateRequestDto {
    @NotNull
    @Min(1)
    private Long id;

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

    @NotNull
    @NotEmpty(message = "Must be at least one role")
    private Set<Long> roles;
}
