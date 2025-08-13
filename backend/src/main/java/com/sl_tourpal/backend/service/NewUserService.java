package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.NewUser;
import com.sl_tourpal.backend.dto.NewUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sl_tourpal.backend.repository.NewUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewUserService {

    @Autowired
    private NewUserRepository newuserRepository;

    public NewUserDTO addUser(NewUserDTO dto) {
        // Check if email already exists
        if (newuserRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(dto.getEmail()))) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }
        
        NewUser newuser = dtoToEntity(dto);
        return toDTO(newuserRepository.save(newuser));
    }

    public List<NewUserDTO> getAllUsers() {
        return newuserRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NewUserDTO getUserById(Long id) {
        NewUser user = newuserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toDTO(user);
    }

    public List<NewUserDTO> getUsersByRole(NewUser.Role role) {
        return newuserRepository.findByRole(role).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NewUserDTO updateUser(Long id, NewUserDTO dto) {
        NewUser existing = newuserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existing.setName(dto.getName());
        existing.setContact(dto.getContact());
        existing.setEmail(dto.getEmail());
        existing.setNic(dto.getNic());
        existing.setRole(dto.getRole());
        return toDTO(newuserRepository.save(existing));
    }

    public void deleteUser(Long id) {
        if (!newuserRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        newuserRepository.deleteById(id);
    }

    // Helper methods
    public List<NewUserDTO> getAllGuides() {
        return getUsersByRole(NewUser.Role.GUIDE);
    }

    public List<NewUserDTO> getAllDrivers() {
        return getUsersByRole(NewUser.Role.DRIVER);
    }

    public boolean existsByEmail(String email) {
        return newuserRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private NewUser dtoToEntity(NewUserDTO dto) {
        return NewUser.builder()
                .id(dto.getId())
                .name(dto.getName())
                .contact(dto.getContact())
                .email(dto.getEmail())
                .nic(dto.getNic())
                .role(dto.getRole())
                .build();
    }

    private NewUserDTO toDTO(NewUser user) {
        return NewUserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .contact(user.getContact())
                .email(user.getEmail())
                .nic(user.getNic())
                .role(user.getRole())
                .build();
    }
}
