package ru.nazarov.man.pre_project.services;

import ru.nazarov.man.pre_project.dto.UserCreateRequestDto;
import ru.nazarov.man.pre_project.dto.*;
import ru.nazarov.man.pre_project.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);
    Optional<User> findUser(Long id);
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    List<User> getUsers(int pageNumber, int pageSize);
    void delete(Long id);
    void deleteAll();

    // DTO
    UserResponseDto toDto(User user);
    UserResponseDto findDtoById(Long id);
    UserResponseDto findDtoByUsername(String username);
    UserResponseDto create(UserCreateRequestDto request);
    UserResponseDto update(UserUpdateRequestDto request);
    List<UserResponseDto> getUsersDto();
    List<UserResponseDto> getUsersDto(int pageNumber, int pageSize);
}
