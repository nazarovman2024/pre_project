package ru.nazarov.man.pre_project.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.nazarov.man.pre_project.entities.*;
import ru.nazarov.man.pre_project.services.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(
            UserService userService,
            RoleService roleService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String manage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("users", userService.getUsers(page, size));
        return "admin/users";
    }

    @GetMapping("/edit")
    public String edit(
            @RequestParam(value = "id", defaultValue = "0") Long id,
            Model model
    ) {
        Optional<User> userOptional = userService.findUser(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("allRoles", roleService.getAll());
            return "admin/edit";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/edit")
    public String save(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam("username") String username,
            @RequestParam("roles") List<Long> roleIds,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
            ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "admin/edit";
        }

        Set<Role> roles = roleIds.stream()
                .map(roleService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userService.save(user);

        return "redirect:/admin/users?success=UserUpdated";
    }

    @GetMapping("/new")
    public String newUser(
            Model model
    ) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAll());
        return "admin/new";
    }

    @PostMapping("/new")
    public String create(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam String confirmPassword,
            @RequestParam("roles") List<Long> roleIds,
            Model model
    ) {
        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAll());
            return "admin/new";
        }

        Set<Role> roles = roleIds.stream()
                .map(roleService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);
        userService.save(user);

        return "redirect:/admin/users?creationSuccess=true";
    }
    @PostMapping("/delete")
    public String deleteUser(
            @RequestParam("id") long id
    ) {
        userService.delete(id);
        return "redirect:/admin/users?deleteSuccess=true";
    }
}
