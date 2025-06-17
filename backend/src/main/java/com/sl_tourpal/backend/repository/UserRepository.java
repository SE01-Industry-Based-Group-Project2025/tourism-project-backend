package com.sl_tourpal.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}
// This interface extends JpaRepository to provide CRUD operations for the User entity.
// It includes a method to find a User by their email, returning an Optional<User>.w