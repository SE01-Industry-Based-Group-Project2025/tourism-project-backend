package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.Payment;
import com.sl_tourpal.backend.domain.Booking;

import java.util.Optional;

/**
 * Service for managing payments
 */
public interface PaymentService {
    
    /**
     * Record a successful payment for a booking
     * @param booking the booking
     * @param paymentIntentId the Stripe payment intent ID
     */
    void recordSuccessfulPayment(Booking booking, String paymentIntentId);
    
    /**
     * Create or update payment record from Stripe event
     * @param booking the booking
     * @param paymentIntentId the Stripe payment intent ID
     * @param amount the payment amount
     * @param currency the currency
     * @param status the payment status
     * @param receiptUrl the receipt URL (optional)
     * @param rawEvent the raw Stripe event JSON
     * @return the payment record
     */
    Payment upsertPayment(Booking booking, String paymentIntentId, 
                         java.math.BigDecimal amount, String currency, 
                         String status, String receiptUrl, String rawEvent);
    
    /**
     * Find payment by booking ID
     * @param bookingId the booking ID
     * @return optional payment
     */
    Optional<Payment> findByBookingId(Long bookingId);
}
