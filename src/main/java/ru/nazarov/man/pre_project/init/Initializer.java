package ru.nazarov.man.pre_project.init;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import ru.nazarov.man.pre_project.entities.*;
import ru.nazarov.man.pre_project.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Initializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Initializer(RoleRepository roleRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        clearDatabase();
        initRole();
        initUser();
    }

    @Transactional
    public void clearDatabase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Transactional
    public void initRole() {
        try {
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }

            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            System.out.println("Roles initialized.");
        } catch (Exception e) {
            System.err.println("Error during role initialization: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void initUser() {
        try {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN not found"));
                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("USER not found"));

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(Set.of(adminRole, userRole));

                userRepository.save(admin);
            }

            System.out.println("Users initialized.");
        } catch (Exception e) {
            System.err.println("Error during user initialization: " + e.getMessage());
            throw e;
        }
    }
}
