package ru.nazarov.man.pre_project.services;

import ru.nazarov.man.pre_project.entities.Role;
import ru.nazarov.man.pre_project.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    void add(User user);
    void update(User user);
    Optional<User> getUser(Long id);
    List<User> getAllUsers();
    List<User> getUsers(int pageNumber, int pageSize);
    void delete(Long id);
}
