package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.Payment;
import com.sl_tourpal.backend.domain.Booking;
import com.sl_tourpal.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Override
    public void recordSuccessfulPayment(Booking booking, String paymentIntentId) {
        log.info("Recording payment for booking ID: {} with payment intent: {}", booking.getId(), paymentIntentId);
        
        // Check if payment already exists (idempotent)
        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            log.info("Payment already recorded for booking: {}, skipping", booking.getId());
            return;
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setProvider("STRIPE");
        payment.setProviderPaymentIntentId(paymentIntentId);
        payment.setAmount(booking.getTotalAmount());
        payment.setCurrency(booking.getCurrency());
        payment.setStatus("succeeded");

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Successfully saved payment: ID={}, Booking={}, Amount={}, Intent={}", 
                savedPayment.getId(), booking.getId(), booking.getTotalAmount(), paymentIntentId);
    }
    
    @Override
    public Payment upsertPayment(Booking booking, String paymentIntentId, 
                                BigDecimal amount, String currency, 
                                String status, String receiptUrl, String rawEvent) {
        
        Payment payment = paymentRepository.findByBookingId(booking.getId())
                .orElse(new Payment());
        
        payment.setBooking(booking);
        payment.setProvider("STRIPE");
        payment.setProviderPaymentIntentId(paymentIntentId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(status);
        payment.setReceiptUrl(receiptUrl);
        payment.setRawEvent(rawEvent);
        
        Payment saved = paymentRepository.save(payment);
        
        log.info("Upserted payment for booking {} with status {} and amount {} {}", 
                booking.getId(), status, amount, currency);
        
        return saved;
    }
}
