package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.*;
import com.sl_tourpal.backend.dto.*;
import com.sl_tourpal.backend.repository.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    
    @Value("${stripe.currency:USD}")
    private String defaultCurrency;
    
    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;
    
    @Override
    public CheckoutSessionResponseDTO createCheckoutSession(BookingRequestDTO request, Long userId) {
        log.info("Creating checkout session for user {} and tour {}", userId, request.getTourId());
        
        // 1. Validate tour exists and is bookable
        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new IllegalArgumentException("Tour not found"));
        
        if (tour.getIsTemplate()) {
            throw new IllegalArgumentException("Template tours cannot be booked");
        }
        
        // Validate tour has a valid price
        if (tour.getPrice() == null || tour.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tour price must be greater than zero for booking");
        }
        
        // 2. Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // 3. Validate date is in availability range
        validateDateAvailability(tour, request.getSelectedDate());
        
        // 4. Check and reserve capacity
        TourDateInventory inventory = inventoryService.getOrCreateInventoryForUpdate(
                request.getTourId(), request.getSelectedDate());
        inventoryService.assertCapacity(inventory, request.getGuestCount());
        
        // 5. Create pending booking
        Booking booking = createPendingBooking(tour, user, request);
        booking = bookingRepository.save(booking);
        
        // 6. Create Stripe checkout session
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(baseUrl + "/booking-success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(baseUrl + "/booking-cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity((long) request.getGuestCount())
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(defaultCurrency.toLowerCase())
                                                    .setUnitAmount(tour.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(tour.getName())
                                                                    .setDescription("Tour booking for " + request.getSelectedDate())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("bookingId", booking.getId().toString())
                    .putMetadata("tourId", request.getTourId().toString())
                    .putMetadata("userId", userId.toString())
                    .build();
            
            Session session = Session.create(params);
            
            // 7. Update booking with session ID
            booking.setCheckoutSessionId(session.getId());
            bookingRepository.save(booking);
            
            log.info("Created checkout session {} for booking {}", session.getId(), booking.getId());
            
            return new CheckoutSessionResponseDTO(session.getId(), session.getUrl(), booking.getId());
            
        } catch (Exception e) {
            log.error("Failed to create Stripe checkout session", e);
            throw new RuntimeException("Failed to create checkout session: " + e.getMessage());
        }
    }
    
    @Override
    public void confirmFromWebhook(String sessionId, String paymentIntentId) {
        log.info("=== WEBHOOK CONFIRMATION START ===");
        log.info("Confirming booking from webhook for session: {} with payment intent: {}", sessionId, paymentIntentId);
        
        try {
            // Search for booking by session ID
            log.info("Searching for booking with checkout session ID: {}", sessionId);
            Booking booking = bookingRepository.findByCheckoutSessionId(sessionId)
                    .orElseThrow(() -> {
                        log.error("BOOKING NOT FOUND for session ID: {}", sessionId);
                        return new IllegalArgumentException("Booking not found for session: " + sessionId);
                    });
            
            log.info("Found booking: ID={}, Status={}, User={}, Tour={}", 
                    booking.getId(), booking.getStatus(), booking.getUser().getEmail(), booking.getTour().getName());
            
            // Idempotency check
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                log.info("Booking {} already confirmed, skipping", booking.getId());
                return;
            }
            
            log.info("Updating booking {} status from {} to CONFIRMED", booking.getId(), booking.getStatus());
            
            // Update booking status
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaymentIntentId(paymentIntentId);
            bookingRepository.save(booking);
            
            log.info("Successfully updated booking {} to CONFIRMED status", booking.getId());
            
            // Update inventory
            log.info("Updating inventory for tour {} on date {}", booking.getTour().getId(), booking.getSelectedDate());
            TourDateInventory inventory = inventoryService.getOrCreateInventoryForUpdate(
                    booking.getTour().getId(), booking.getSelectedDate());
            inventoryService.commitBooking(inventory, booking.getGuestCount());
            
            log.info("Successfully updated inventory for {} guests", booking.getGuestCount());
            
            // Record payment
            log.info("Recording payment for booking {} with payment intent {}", booking.getId(), paymentIntentId);
            paymentService.recordSuccessfulPayment(booking, paymentIntentId);
            
            log.info("=== WEBHOOK CONFIRMATION COMPLETED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            log.error("=== WEBHOOK CONFIRMATION FAILED ===");
            log.error("Error confirming booking from webhook: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public void handlePaymentFailure(String sessionId, String failureReason) {
        log.info("Handling payment failure for session {}: {}", sessionId, failureReason);
        
        bookingRepository.findByCheckoutSessionId(sessionId)
                .ifPresent(booking -> {
                    booking.setStatus(BookingStatus.FAILED);
                    bookingRepository.save(booking);
                    log.info("Marked booking {} as failed", booking.getId());
                });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> getMyBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toBookingResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getMyBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found or access denied"));
        return toBookingResponseDTO(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AdminBookingListDTO> adminListBookings(Long tourId, LocalDate selectedDate, 
                                                      BookingStatus status, String userEmail, 
                                                      Pageable pageable) {
        return bookingRepository.findByFilters(tourId, selectedDate, status, userEmail, pageable)
                .map(this::toAdminBookingListDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AdminBookingListDTO adminGetBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return toAdminBookingListDTO(booking);
    }
    
    @Override
    public void adminCancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // If booking was confirmed, adjust inventory
        if (oldStatus == BookingStatus.CONFIRMED) {
            TourDateInventory inventory = inventoryService.getOrCreateInventoryForUpdate(
                    booking.getTour().getId(), booking.getSelectedDate());
            inventory.setBooked(Math.max(0, inventory.getBooked() - booking.getGuestCount()));
            log.info("Adjusted inventory for cancelled booking {}", bookingId);
        }
        
        log.info("Admin cancelled booking {}", bookingId);
    }
    
    private void validateDateAvailability(Tour tour, LocalDate selectedDate) {
        if (tour.getAvailabilityRanges() == null || tour.getAvailabilityRanges().isEmpty()) {
            throw new IllegalArgumentException("Tour has no availability ranges configured");
        }
        
        boolean dateAvailable = tour.getAvailabilityRanges().stream()
                .anyMatch(range -> !selectedDate.isBefore(range.getStartDate()) && 
                                 !selectedDate.isAfter(range.getEndDate()));
        
        if (!dateAvailable) {
            throw new IllegalArgumentException("Selected date is not available for this tour");
        }
    }
    
    private Booking createPendingBooking(Tour tour, User user, BookingRequestDTO request) {
        BigDecimal unitPrice = tour.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(request.getGuestCount()));
        
        Booking booking = new Booking();
        booking.setTour(tour);
        booking.setUser(user);
        booking.setTourNameSnapshot(tour.getName());
        booking.setUnitPriceSnapshot(unitPrice);
        booking.setCurrency(defaultCurrency);
        booking.setSelectedDate(request.getSelectedDate());
        booking.setGuestCount(request.getGuestCount());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);
        booking.setSpecialNote(request.getSpecialNote());
        
        return booking;
    }
    
    private BookingResponseDTO toBookingResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getTour().getId(),
                booking.getTourNameSnapshot(),
                booking.getSelectedDate(),
                booking.getGuestCount(),
                booking.getTotalAmount(),
                booking.getCurrency(),
                booking.getStatus(),
                booking.getSpecialNote(),
                booking.getCreatedAt()
        );
    }
    
    private AdminBookingListDTO toAdminBookingListDTO(Booking booking) {
        String paymentStatus = null; // TODO: Add payment status lookup if needed
        
        return new AdminBookingListDTO(
                booking.getId(),
                booking.getTour().getId(),
                booking.getTourNameSnapshot(),
                booking.getUser().getId(),
                booking.getUser().getEmail(),
                booking.getUser().getFirstName() + " " + booking.getUser().getLastName(),
                booking.getSelectedDate(),
                booking.getGuestCount(),
                booking.getTotalAmount(),
                booking.getCurrency(),
                booking.getStatus(),
                paymentStatus,
                booking.getCreatedAt()
        );
    }
}
