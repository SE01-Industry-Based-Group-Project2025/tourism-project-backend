package com.sl_tourpal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.TourImage;

public interface TourImageRepository extends JpaRepository<TourImage, Long> {}