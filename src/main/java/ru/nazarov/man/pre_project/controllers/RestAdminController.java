package ru.nazarov.man.pre_project.controllers;

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

    @GetMapping("/users")
    public List<UserResponseDto> usersList(
    ) {
        return userService.getUsersDto();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUser(
            @PathVariable Long id
    ) {
        return userService.findUser(id)
                .map(user -> ResponseEntity.ok(userService.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(
            @RequestBody @Valid UserCreateRequestDto request
    ) {
        return userService.create(request);
    }

    @PatchMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(
            @RequestBody @Valid UserUpdateRequestDto request
    ) {
        return userService.update(request);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long id
    ) {
        userService.delete(id);
    }

    @GetMapping("/roles")
    public List<RoleResponseDto> rolesList() {
        return roleService.getRolesDto();
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponseDto> getRole(
            @PathVariable Long id
    ) {
        return roleService.findById(id)
                .map(role -> ResponseEntity.ok(roleService.toDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }
}
