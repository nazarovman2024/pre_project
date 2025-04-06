package ru.nazarov.man.pre_project.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nazarov.man.pre_project.dto.*;
import ru.nazarov.man.pre_project.entities.Role;
import ru.nazarov.man.pre_project.entities.User;
import ru.nazarov.man.pre_project.repositories.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Autowired
    public UserServiceImp(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(usr -> Hibernate.initialize(usr.getRoles()));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<User> users = userRepository.findAll(pageable).getContent();
        users.forEach(user -> Hibernate.initialize(user.getRoles())); // Корявая затычка, но как сделать лучше, я не придумал...
        return users;
    }

    @Override
    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("User with ID " + id + " not found", e);
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    // DTO
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto toDto(@NotNull User user) {
        final UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoles(
                user.getRoles().stream()
                        .map(Role::getId)
                        .collect(Collectors.toSet())
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findDtoById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with ID: " + id
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findDtoByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with username: " + username
                ));
    }

    @Override
    public UserResponseDto create(UserCreateRequestDto request) {

        final User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(
                request.getRoles().stream()
                        .map(roleRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet())
        );

        return toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto update(
            UserUpdateRequestDto request
    ) {
        Optional<User> userOptional = userRepository.findById(request.getId());
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + request.getId()));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRoles(
                request.getRoles().stream()
                        .map(roleRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet())
        );

        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersDto() {
        List<User> users = getAllUsers();
        return users == null ? Collections.emptyList()
                : users.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersDto(int pageNumber, int pageSize) {
        List<User> users = getUsers(pageNumber, pageSize);
        return users == null ? Collections.emptyList()
                : users.stream().map(this::toDto).toList();
    }
}