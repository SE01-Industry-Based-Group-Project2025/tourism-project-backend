package com.sl_tourpal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.domain.User;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {
    // Custom tour queries
    List<Tour> findByIsCustomTrue();

    List<Tour> findByIsCustomTrueAndStatus(String status);

    List<Tour> findByIsCustomFalse();

    List<Tour> findByStatus(String status);

    List<Tour> findByIsCustomTrueAndCreatedBy(User createdBy);

    List<Tour> findByCreatedBy(User createdBy);

    List<Tour> findByIsCustomTrueAndCreatedByOrderByCreatedAtDesc(User createdBy);
    
    // Template/Tour queries
    List<Tour> findByIsTemplate(boolean isTemplate);
}