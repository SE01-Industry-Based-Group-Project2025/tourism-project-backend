package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private Long totalBookings;
    private Long totalUsers;
    private Long totalTours;
    private BigDecimal totalRevenue;
    private Long activeBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private BigDecimal monthlyRevenue;
    private Long monthlyBookings;
}
