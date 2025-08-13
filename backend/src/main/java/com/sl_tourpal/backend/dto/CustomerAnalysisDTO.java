package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAnalysisDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Long totalBookings;
    private BigDecimal totalSpent;
    private BigDecimal averageBookingValue;
    private String customerTier; // "BRONZE", "SILVER", "GOLD", "PLATINUM"
}
