package com.sl_tourpal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sl_tourpal.backend.domain.PricingTier;

public interface PricingTierRepository extends JpaRepository<PricingTier, Long> {}