package com.sl_tourpal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {}