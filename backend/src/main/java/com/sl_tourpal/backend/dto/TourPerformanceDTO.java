package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourPerformanceDTO {
    private Long tourId;
    private String tourName;
    private String category;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageBookingValue;
}
