package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.Booking;
import com.sl_tourpal.backend.domain.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
    
    Optional<Booking> findByCheckoutSessionId(String checkoutSessionId);
    
    Optional<Booking> findByPaymentIntentId(String paymentIntentId);
    
    @Query("SELECT b FROM Booking b WHERE " +
           "(:tourId IS NULL OR b.tour.id = :tourId) AND " +
           "(:selectedDate IS NULL OR b.selectedDate = :selectedDate) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:userEmail IS NULL OR LOWER(b.user.email) LIKE LOWER(CONCAT('%', :userEmail, '%')))")
    Page<Booking> findByFilters(@Param("tourId") Long tourId,
                               @Param("selectedDate") LocalDate selectedDate,
                               @Param("status") BookingStatus status,
                               @Param("userEmail") String userEmail,
                               Pageable pageable);
    
    List<Booking> findByTourIdAndSelectedDateAndStatus(Long tourId, LocalDate selectedDate, BookingStatus status);
}
