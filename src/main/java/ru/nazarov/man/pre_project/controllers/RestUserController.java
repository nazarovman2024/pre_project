package ru.nazarov.man.pre_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.nazarov.man.pre_project.dto.UserResponseDto;
import ru.nazarov.man.pre_project.entities.User;
import ru.nazarov.man.pre_project.services.UserService;

@RestController
@RequestMapping("/api/users")
public class RestUserController {

    private final UserService userService;

    @Autowired
    public RestUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponseDto> getCurrentUser(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return userService.findUser(user.getId())
                .map(u -> ResponseEntity.ok(userService.toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }
}