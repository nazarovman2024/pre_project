package ru.nazarov.man.pre_project.services;

import org.springframework.data.jpa.repository.EntityGraph;
import ru.nazarov.man.pre_project.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);
    Optional<Role> findById(Long id);
    List<Role> getAll();
    void save(Role role);
    void deleteAll();
}
