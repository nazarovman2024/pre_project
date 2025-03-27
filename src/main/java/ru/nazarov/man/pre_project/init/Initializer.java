package ru.nazarov.man.pre_project.init;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import ru.nazarov.man.pre_project.entities.*;
import ru.nazarov.man.pre_project.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.logging.Logger;

@Component
@Transactional
public class Initializer {

    private static final Logger logger = Logger.getLogger(String.valueOf(Initializer.class));

    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Initializer(RoleService roleService,
                       UserService userService,
                       PasswordEncoder passwordEncoder
    ) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        clearDatabase();
        initRole();
        initUser();
    }

    public void clearDatabase() {
        userService.deleteAll();
        roleService.deleteAll();
    }

    public void initRole() {
        try {
            if (roleService.findByName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleService.save(userRole);
            }

            if (roleService.findByName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleService.save(adminRole);
            }

            logger.info("Roles initialized.");
        } catch (Exception e) {
            logger.warning("Error during role initialization: " + e.getMessage());
            throw e;
        }
    }

    public void initUser() {
        try {
            if (userService.findByUsername("admin").isEmpty()) {
                Role adminRole = roleService.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN not found"));
                Role userRole = roleService.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("USER not found"));

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(Set.of(adminRole, userRole));

                userService.save(admin);
            }

            logger.info("Users initialized.");
        } catch (Exception e) {
            logger.warning("Error during user initialization: " + e.getMessage());
            throw e;
        }
    }
}
