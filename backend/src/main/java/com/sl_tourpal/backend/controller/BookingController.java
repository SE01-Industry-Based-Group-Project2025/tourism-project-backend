package com.sl_tourpal.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sl_tourpal.backend.dto.BookingRequestDTO;
import com.sl_tourpal.backend.dto.BookingResponseDTO;
import com.sl_tourpal.backend.dto.CheckoutSessionResponseDTO;
import com.sl_tourpal.backend.security.CustomUserDetails;
import com.sl_tourpal.backend.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    
    /**
     * Test endpoint to verify controller is working
     * GET /api/bookings/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("BookingController is working!");
    }
    
    /**
     * Create a Stripe checkout session for booking
     * POST /api/bookings/checkout-session
     */
    @PostMapping("/checkout-session")
    // @PreAuthorize("hasRole('USER')") // TODO: Fix authorization issue
    public ResponseEntity<CheckoutSessionResponseDTO> createCheckoutSession(
            @Valid @RequestBody BookingRequestDTO request) {
        
        Long userId = getCurrentUserId();
        CheckoutSessionResponseDTO response = bookingService.createCheckoutSession(request, userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user's bookings
     * GET /api/bookings/my
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookings(Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<BookingResponseDTO> bookings = bookingService.getMyBookings(userId, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get current user's bookings (alternative endpoint)
     * GET /api/bookings/my-bookings
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookingsAlternative(Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<BookingResponseDTO> bookings = bookingService.getMyBookings(userId, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get specific booking by ID (user can only access their own bookings)
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        BookingResponseDTO booking = bookingService.getMyBooking(id, userId);
        return ResponseEntity.ok(booking);
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getId();
        }
        throw new RuntimeException("Unable to get current user ID");
    }
}
