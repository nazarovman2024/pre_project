package ru.nazarov.man.pre_project.services;

import ru.nazarov.man.pre_project.dto.RoleResponseDto;
import ru.nazarov.man.pre_project.entities.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);
    Optional<Role> findById(Long id);
    List<Role> getAll();
    void save(Role role);
    void deleteAll();

    // DTO
    RoleResponseDto toDto(Role role);
    List<RoleResponseDto> getRolesDto();
    RoleResponseDto findDtoById(Long id);
    RoleResponseDto findDtoByName(String name);
    List<RoleResponseDto> findDtoByIds(Collection<Long> ids);
    List<RoleResponseDto> findDtoByNames(Collection<String> names);
}
