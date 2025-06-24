package com.sl_tourpal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {}