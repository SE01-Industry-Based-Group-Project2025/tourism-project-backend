package com.sl_tourpal.backend.dto;

import com.sl_tourpal.backend.domain.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    
    private Long id;
    private Long tourId;
    private String tourName;
    private LocalDate selectedDate;
    private int guestCount;
    private BigDecimal totalAmount;
    private String currency;
    private BookingStatus status;
    private String specialNote;
    private LocalDateTime createdAt;
    private PaymentSummaryDTO payment;
}
