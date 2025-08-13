package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingTrendDTO {
    private Integer year;
    private Integer month;
    private String monthName;
    private Long bookingCount;
    private BigDecimal averageBookingValue;
    private String trendStatus; // "UP", "DOWN", "STABLE"
}
