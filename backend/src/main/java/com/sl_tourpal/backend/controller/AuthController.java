package com.sl_tourpal.backend.controller;

import com.sl_tourpal.backend.domain.Role;
import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.dto.AuthResponse;
import com.sl_tourpal.backend.dto.RegisterRequest;
import com.sl_tourpal.backend.dto.UserResponseDTO;
import com.sl_tourpal.backend.repository.RoleRepository;
import com.sl_tourpal.backend.repository.UserRepository;
import com.sl_tourpal.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo,
            RoleRepository roleRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        // 1. Check email uniqueness
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email is already in use");
        }

        // 2. Determine role (use provided or default to ROLE_USER)
        String roleName = (req.getRole() != null && !req.getRole().isBlank())
                ? req.getRole()
                : "ROLE_USER";
        Role role = roleRepo.findByName(roleName)
                .orElseGet(() -> roleRepo.findByName("ROLE_USER")
                        .orElseThrow(() -> new IllegalStateException("ROLE_USER not found in database")));

        // 3. Create and save user
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPhone(req.getPhone());
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        userRepo.save(user);

        // 4. Generate token
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(
                                user.getRoles().stream()
                                        .map(Role::getName)
                                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList()))
                        .build());

        // 5. Build response DTO
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless JWT → just return 204
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String authHeader) {
        // Extract token
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        String email = jwtUtil.extractUsername(token);
        return userRepo.findByEmail(email)
            .map(user -> {
                UserResponseDTO dto = new UserResponseDTO();
                dto.setId(user.getId());
                dto.setEmail(user.getEmail());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setPhone(user.getPhone());
                dto.setEnabled(user.isEnabled());
                dto.setRoles(user.getRoles().stream()
                                 .map(Role::getName)
                                 .collect(Collectors.toSet()));
                return ResponseEntity.ok((Object) dto);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                           .body("Invalid token"));
    }
}
