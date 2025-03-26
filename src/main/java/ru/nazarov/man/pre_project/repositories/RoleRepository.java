package ru.nazarov.man.pre_project.repositories;

import ru.nazarov.man.pre_project.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
