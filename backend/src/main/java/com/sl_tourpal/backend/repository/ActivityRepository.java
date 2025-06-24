package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
