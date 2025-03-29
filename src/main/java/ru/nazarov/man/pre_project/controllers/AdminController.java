package ru.nazarov.man.pre_project.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.nazarov.man.pre_project.entities.*;
import ru.nazarov.man.pre_project.services.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(
            UserService userService,
            RoleService roleService
    ) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String manage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("users", userService.getUsers(page, size));
        model.addAttribute("allRoles", roleService.getAll());
        model.addAttribute("userModel", new User());
        return "admin/admin";
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
        return "admin/admin";
        } else {
            return "redirect:/admin";
        }
    }

    @PostMapping()
    public String save(
            @RequestParam("id") Long id,
            @RequestParam List<Long> roles,
            @RequestParam(required = false, defaultValue = "") String password,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/admin";
        }

        Optional<User> userOptional = userService.findUser(id);
        if (userOptional.isEmpty()) {
            return "redirect:/admin";
        }

        userService.edit(id, password, roles);

        return "redirect:/admin?success=UserUpdated";
    }

    @PostMapping("/user/new")
    public String create(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam List<Long> roles,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAll());
            return "admin/admin";
        }

        userService.add(username, password, roles);

        return "redirect:/admin?creationSuccess=true";
    }

    @PostMapping("/user/delete")
    public String deleteUser(
            @RequestParam("id") long id
    ) {
        userService.delete(id);
        return "redirect:/admin?deleteSuccess=true";
    }
}
