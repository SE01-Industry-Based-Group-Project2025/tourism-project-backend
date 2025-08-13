package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.NewUser;
import com.sl_tourpal.backend.domain.NewUser.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewUserRepository extends JpaRepository<NewUser, Long> {
    List<NewUser> findByRole(Role role);
}
