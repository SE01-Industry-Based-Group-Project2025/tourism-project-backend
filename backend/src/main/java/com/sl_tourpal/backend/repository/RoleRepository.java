package com.sl_tourpal.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
// This code defines a repository interface for the Role entity, extending JpaRepository to provide CRUD operations.
// It includes a method to find a Role by its name, returning an Optional<Role>.