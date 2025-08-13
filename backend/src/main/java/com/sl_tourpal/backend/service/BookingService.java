package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.dto.*;
import com.sl_tourpal.backend.domain.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Service for managing tour bookings and Stripe integration
 */
public interface BookingService {
    
    /**
     * Create a Stripe checkout session for a booking
     * @param request the booking request
     * @param userId the current user ID
     * @return checkout session response with URL
     */
    CheckoutSessionResponseDTO createCheckoutSession(BookingRequestDTO request, Long userId);
    
    /**
     * Confirm booking from Stripe webhook
     * @param sessionId the Stripe session ID
     * @param paymentIntentId the Stripe payment intent ID
     */
    void confirmFromWebhook(String sessionId, String paymentIntentId);
    
    /**
     * Confirm booking from Stripe webhook with receipt URL
     * @param sessionId the Stripe session ID
     * @param paymentIntentId the Stripe payment intent ID
     * @param receiptUrl the receipt URL from Stripe
     * @param rawEvent the raw Stripe event JSON
     */
    void confirmFromWebhook(String sessionId, String paymentIntentId, String receiptUrl, String rawEvent);
    
    /**
     * Handle payment failure from Stripe webhook
     * @param sessionId the Stripe session ID
     * @param failureReason the failure reason
     */
    void handlePaymentFailure(String sessionId, String failureReason);
    
    /**
     * Get current user's bookings
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of user's bookings
     */
    Page<BookingResponseDTO> getMyBookings(Long userId, Pageable pageable);
    
    /**
     * Get specific booking for current user
     * @param bookingId the booking ID
     * @param userId the user ID (for security)
     * @return booking details
     */
    BookingResponseDTO getMyBooking(Long bookingId, Long userId);
    
    /**
     * Admin: Get all bookings with filters
     * @param tourId filter by tour ID (optional)
     * @param selectedDate filter by date (optional)
     * @param status filter by status (optional)
     * @param userEmail filter by user email (optional)
     * @param pageable pagination info
     * @return page of booking results
     */
    Page<AdminBookingListDTO> adminListBookings(Long tourId, LocalDate selectedDate, 
                                               BookingStatus status, String userEmail, 
                                               Pageable pageable);
    
    /**
     * Admin: Get booking details
     * @param bookingId the booking ID
     * @return booking details
     */
    AdminBookingListDTO adminGetBooking(Long bookingId);
    
    /**
     * Admin: Cancel a booking
     * @param bookingId the booking ID
     */
    void adminCancelBooking(Long bookingId);
}
