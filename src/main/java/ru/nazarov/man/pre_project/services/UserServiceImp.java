package ru.nazarov.man.pre_project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nazarov.man.pre_project.entities.User;
import ru.nazarov.man.pre_project.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void add(User user) {
        if (user.getId() == null) {
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User ID must be null for add operation");
        }
    }

    @Override
    public void update(User user) {
        if (user.getId() != null) {
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User ID must not be null for update operation");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("User with ID " + id + " not found", e);
        }
    }
}
