package com.sl_tourpal.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
  Optional<Privilege> findByName(String name);
}
// This interface extends JpaRepository to provide CRUD operations for the Privilege entity.
// It includes a method to find a Privilege by its name, returning an Optional<Privilege>.