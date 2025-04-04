package ru.nazarov.man.pre_project.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nazarov.man.pre_project.services.*;
import ru.nazarov.man.pre_project.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class RestAdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RestAdminController(
            UserService userService,
            RoleService roleService
    ) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Operation(
            summary = "Get users list",
            description = "Returns paginated list of users. Page and size are optional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params")
    })
    @GetMapping("/users")
    public List<UserResponseDto> usersList(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(required = false)
            @Min(value = 1, message = "Page must be at least 1")
            Integer page,

            @Parameter(description = "Items per page (1-100)", example = "10")
            @RequestParam(required = false)
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size cannot exceed 100")
            Integer size
    ) {
        return ((page != null) || (size != null))
                ? userService.getUsersDto(
                page != null ? page : 1,
                size != null ? size : 10
        )
                : userService.getUsersDto();
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUser(
            @Parameter(description = "User ID", example = "123")
            @PathVariable Long id
    ) {
        return userService.findUser(id)
                .map(user -> ResponseEntity.ok(userService.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create user",
            description = "Returns created user with generated ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserCreateRequestDto.class))
            )
            @RequestBody @Valid UserCreateRequestDto request
    ) {
        return userService.create(request);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user data")
    public UserResponseDto updateUser(
            @RequestBody @Valid @Schema(description = "User update data")
            UserUpdateRequestDto request
    ) {
        return userService.update(request);
    }

    @Operation(
            summary = "Delete user by ID",
            description = "Permanently removes user with specified ID. Returns no content."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(hidden = true))  // Пустое тело ошибки
            )
    })
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "ID of user to delete", example = "123", required = true)
            @PathVariable Long id
    ) {
        userService.delete(id);
    }

    @GetMapping("/roles")
    public List<RoleResponseDto> rolesList() {
        return roleService.getRolesDto();
    }

    @Operation(summary = "Get role by ID", description = "Returns role details by specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponseDto> getRole(
            @Parameter(description = "ID of the role to retrieve", example = "1", required = true)
            @PathVariable Long id) {

        return roleService.findById(id)
                .map(role -> ResponseEntity.ok(roleService.toDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }
}
