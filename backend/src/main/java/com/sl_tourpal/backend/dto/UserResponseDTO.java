package com.sl_tourpal.backend.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean enabled;
    private Set<String> roles;
}
