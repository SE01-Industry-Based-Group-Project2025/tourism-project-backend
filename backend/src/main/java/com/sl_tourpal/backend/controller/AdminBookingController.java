package com.sl_tourpal.backend.controller;

import com.sl_tourpal.backend.dto.AdminBookingListDTO;
import com.sl_tourpal.backend.domain.BookingStatus;
import com.sl_tourpal.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Admin controller for booking management
 */
@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminBookingController {
    
    private final BookingService bookingService;
    
    /**
     * Get all bookings with filters
     * GET /api/admin/bookings
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminBookingListDTO>> getAllBookings(
            @RequestParam(required = false) Long tourId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) String userEmail,
            Pageable pageable) {
        
        Page<AdminBookingListDTO> bookings = bookingService.adminListBookings(
                tourId, selectedDate, status, userEmail, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get booking details
     * GET /api/admin/bookings/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminBookingListDTO> getBooking(@PathVariable Long id) {
        AdminBookingListDTO booking = bookingService.adminGetBooking(id);
        return ResponseEntity.ok(booking);
    }
    
    /**
     * Cancel a booking
     * POST /api/admin/bookings/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        bookingService.adminCancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
