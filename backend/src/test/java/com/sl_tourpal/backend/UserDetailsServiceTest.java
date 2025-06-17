package com.sl_tourpal.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.repository.UserRepository;
import com.sl_tourpal.backend.security.CustomUserDetailsService;

@SpringBootTest
class UserDetailsServiceTest {
  @Autowired
  CustomUserDetailsService service;

  @Autowired
  UserRepository userRepository;

  @Test
  void loadUserByUsername_ExistingUser() {
    // Insert a test user first
    User u = new User();
    u.setEmail("test@example.com");
    u.setPassword("$2a$10$7G6..."); // a BCrypt hash
    u.setEnabled(true);
    userRepository.save(u);

    UserDetails ud = service.loadUserByUsername("test@example.com");
    assertEquals("test@example.com", ud.getUsername());
  }
}
