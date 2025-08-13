package com.sl_tourpal.backend.controller;

import com.sl_tourpal.backend.dto.*;
import com.sl_tourpal.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        log.info("Admin requested dashboard summary");
        DashboardSummaryDTO summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<MonthlyRevenueDTO>> getMonthlyRevenue(
            @RequestParam(defaultValue = "12") int months) {
        log.info("Admin requested monthly revenue for {} months", months);
        
        if (months < 1 || months > 24) {
            months = 12; // Default to 12 months if invalid
        }
        
        List<MonthlyRevenueDTO> monthlyRevenue = analyticsService.getMonthlyRevenue(months);
        return ResponseEntity.ok(monthlyRevenue);
    }

    @GetMapping("/tours/performance")
    public ResponseEntity<List<TourPerformanceDTO>> getTourPerformance(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Admin requested tour performance for top {} tours", limit);
        
        if (limit < 1 || limit > 50) {
            limit = 10; // Default to 10 if invalid
        }
        
        List<TourPerformanceDTO> tourPerformance = analyticsService.getTourPerformance(limit);
        return ResponseEntity.ok(tourPerformance);
    }

    @GetMapping("/customers/analysis")
    public ResponseEntity<List<CustomerAnalysisDTO>> getCustomerAnalysis(
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Admin requested customer analysis for top {} customers", limit);
        
        if (limit < 1 || limit > 100) {
            limit = 20; // Default to 20 if invalid
        }
        
        List<CustomerAnalysisDTO> customerAnalysis = analyticsService.getCustomerAnalysis(limit);
        return ResponseEntity.ok(customerAnalysis);
    }

    @GetMapping("/bookings/trends")
    public ResponseEntity<List<BookingTrendDTO>> getBookingTrends(
            @RequestParam(defaultValue = "6") int months) {
        log.info("Admin requested booking trends for {} months", months);
        
        if (months < 1 || months > 24) {
            months = 6; // Default to 6 months if invalid
        }
        
        List<BookingTrendDTO> bookingTrends = analyticsService.getBookingTrends(months);
        return ResponseEntity.ok(bookingTrends);
    }
}
