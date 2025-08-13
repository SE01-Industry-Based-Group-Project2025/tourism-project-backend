package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private int year;
    private int month;
    private String monthName;
    private BigDecimal revenue;
    private Long bookingCount;
}
