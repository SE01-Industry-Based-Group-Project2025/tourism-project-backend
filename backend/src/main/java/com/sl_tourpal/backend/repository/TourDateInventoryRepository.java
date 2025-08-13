package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.TourDateInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TourDateInventoryRepository extends JpaRepository<TourDateInventory, Long> {
    
    Optional<TourDateInventory> findByTourIdAndDate(Long tourId, LocalDate date);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tdi FROM TourDateInventory tdi WHERE tdi.tour.id = :tourId AND tdi.date = :date")
    Optional<TourDateInventory> findByTourIdAndDateForUpdate(@Param("tourId") Long tourId, 
                                                            @Param("date") LocalDate date);
}
