package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    // Find activities by multiple regions
    List<Activity> findByRegionIn(List<String> regions);
    
    // Find activities by single region
    List<Activity> findByRegion(String region);
    
    // Find activities by region containing keyword (case-insensitive)
    List<Activity> findByRegionContainingIgnoreCase(String region);
}
