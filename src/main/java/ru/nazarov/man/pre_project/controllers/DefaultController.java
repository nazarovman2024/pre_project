package ru.nazarov.man.pre_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.nazarov.man.pre_project.entities.*;
import ru.nazarov.man.pre_project.services.*;

import java.util.Set;

@Controller
public class DefaultController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultController(
            UserService userService,
            RoleService roleService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registration")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "registration";
        }

        if (userService.findByUsername(username).isEmpty()) {
            Role userRole = roleService.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(Set.of(userRole));

            userService.save(user);
        } else {
            model.addAttribute(
                    "errorMessage",
                    "User \"" + username + "\" already exists.");
            return "registration";
        }

        return "redirect:/login?registrationSuccess=true";
    }

    @GetMapping(value = "/profile")
    public String showProfile(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "user/profile";
    }
}
