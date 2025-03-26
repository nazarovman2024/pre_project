package ru.nazarov.man.pre_project.repositories;

import org.springframework.data.jpa.repository.*;
import ru.nazarov.man.pre_project.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(String username);
}
