package ru.nazarov.man.pre_project.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nazarov.man.pre_project.dto.RoleResponseDto;
import ru.nazarov.man.pre_project.entities.Role;
import ru.nazarov.man.pre_project.repositories.RoleRepository;

import java.util.*;

@Service
@Transactional
public class RoleServiceImp implements RoleService{
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void deleteAll() {
        roleRepository.deleteAll();
    }

    // DTO
    @Override
    public RoleResponseDto toDto(@NotNull Role role) {
        final RoleResponseDto response = new RoleResponseDto();

        response.setId(role.getId());
        response.setName(role.getName());

        return response;
    }

    @Override
    public List<RoleResponseDto> getRolesDto() {
        return roleRepository.findAll().stream()
                .map(this::toDto).toList();
    }

    @Override
    public RoleResponseDto findDtoById(Long id) {
        return roleRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role not found with ID: " + id
                ));
    }

    @Override
    public RoleResponseDto findDtoByName(String name) {
        return roleRepository.findByName(name)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Role not found with name: " + name
                ));
    }

    @Override
    public List<RoleResponseDto> findDtoByIds(Collection<Long> ids) {
        return getRolesDto().stream()
                .filter(roleDto -> ids.contains(roleDto.getId()))
                .toList();
    }

    @Override
    public List<RoleResponseDto> findDtoByNames(Collection<String> names) {
        return getRolesDto().stream()
                .filter(role -> names.contains(role.getName()))
                .toList();
    }
}