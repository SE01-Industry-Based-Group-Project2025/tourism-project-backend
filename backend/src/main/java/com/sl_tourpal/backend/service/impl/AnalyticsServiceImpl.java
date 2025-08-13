package com.sl_tourpal.backend.service.impl;

import com.sl_tourpal.backend.dto.*;
import com.sl_tourpal.backend.repository.AnalyticsRepository;
import com.sl_tourpal.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue(int months) {
        log.info("Fetching monthly revenue for last {} months", months);
        
        List<Object[]> results = analyticsRepository.getMonthlyRevenue(months);
        List<MonthlyRevenueDTO> monthlyRevenue = new ArrayList<>();

        for (Object[] result : results) {
            MonthlyRevenueDTO dto = new MonthlyRevenueDTO();
            dto.setYear((Integer) result[0]);
            dto.setMonth((Integer) result[1]);
            dto.setMonthName((String) result[2]);
            dto.setRevenue((BigDecimal) result[3]);
            dto.setBookingCount(((Number) result[4]).longValue());
            monthlyRevenue.add(dto);
        }

        log.info("Retrieved {} monthly revenue records", monthlyRevenue.size());
        return monthlyRevenue;
    }

    @Override
    public List<TourPerformanceDTO> getTourPerformance(int limit) {
        log.info("Fetching tour performance data for top {} tours", limit);
        
        List<Object[]> results = analyticsRepository.getTourPerformance(limit);
        List<TourPerformanceDTO> tourPerformance = new ArrayList<>();

        for (Object[] result : results) {
            TourPerformanceDTO dto = new TourPerformanceDTO();
            dto.setTourId(((Number) result[0]).longValue());
            dto.setTourName((String) result[1]);
            dto.setCategory((String) result[2]);
            dto.setTotalBookings(((Number) result[3]).longValue());
            dto.setTotalRevenue((BigDecimal) result[4]);
            dto.setAverageBookingValue(((BigDecimal) result[5]).setScale(2, RoundingMode.HALF_UP));
            tourPerformance.add(dto);
        }

        log.info("Retrieved {} tour performance records", tourPerformance.size());
        return tourPerformance;
    }

    @Override
    public List<CustomerAnalysisDTO> getCustomerAnalysis(int limit) {
        log.info("Fetching customer analysis for top {} customers", limit);
        
        List<Object[]> results = analyticsRepository.getCustomerAnalysis(limit);
        List<CustomerAnalysisDTO> customerAnalysis = new ArrayList<>();

        for (Object[] result : results) {
            CustomerAnalysisDTO dto = new CustomerAnalysisDTO();
            dto.setUserId(((Number) result[0]).longValue());
            dto.setFirstName((String) result[1]);
            dto.setLastName((String) result[2]);
            dto.setEmail((String) result[3]);
            dto.setTotalBookings(((Number) result[4]).longValue());
            dto.setTotalSpent((BigDecimal) result[5]);
            dto.setAverageBookingValue(((BigDecimal) result[6]).setScale(2, RoundingMode.HALF_UP));
            
            // Calculate customer tier based on total spent
            BigDecimal totalSpent = dto.getTotalSpent();
            if (totalSpent.compareTo(new BigDecimal("10000")) >= 0) {
                dto.setCustomerTier("PLATINUM");
            } else if (totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
                dto.setCustomerTier("GOLD");
            } else if (totalSpent.compareTo(new BigDecimal("2000")) >= 0) {
                dto.setCustomerTier("SILVER");
            } else {
                dto.setCustomerTier("BRONZE");
            }
            
            customerAnalysis.add(dto);
        }

        log.info("Retrieved {} customer analysis records", customerAnalysis.size());
        return customerAnalysis;
    }

    @Override
    public List<BookingTrendDTO> getBookingTrends(int months) {
        log.info("Fetching booking trends for last {} months", months);
        
        List<Object[]> results = analyticsRepository.getBookingTrends(months);
        List<BookingTrendDTO> bookingTrends = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            Object[] result = results.get(i);
            BookingTrendDTO dto = new BookingTrendDTO();
            dto.setYear((Integer) result[0]);
            dto.setMonth((Integer) result[1]);
            dto.setMonthName((String) result[2]);
            dto.setBookingCount(((Number) result[3]).longValue());
            dto.setAverageBookingValue(((BigDecimal) result[4]).setScale(2, RoundingMode.HALF_UP));
            
            // Calculate trend status by comparing with previous month
            if (i < results.size() - 1) {
                Long currentBookings = dto.getBookingCount();
                Long previousBookings = ((Number) results.get(i + 1)[3]).longValue();
                
                if (currentBookings > previousBookings) {
                    dto.setTrendStatus("UP");
                } else if (currentBookings < previousBookings) {
                    dto.setTrendStatus("DOWN");
                } else {
                    dto.setTrendStatus("STABLE");
                }
            } else {
                dto.setTrendStatus("STABLE"); // First month or no previous data
            }
            
            bookingTrends.add(dto);
        }

        log.info("Retrieved {} booking trend records", bookingTrends.size());
        return bookingTrends;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        log.info("Fetching dashboard summary data");
        
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
        summary.setTotalBookings(analyticsRepository.getTotalBookings());
        summary.setTotalUsers(analyticsRepository.getTotalUsers());
        summary.setTotalTours(analyticsRepository.getTotalTours());
        summary.setTotalRevenue(analyticsRepository.getTotalRevenue());
        summary.setActiveBookings(analyticsRepository.getActiveBookings());
        summary.setCompletedBookings(analyticsRepository.getCompletedBookings());
        summary.setCancelledBookings(analyticsRepository.getCancelledBookings());
        summary.setMonthlyRevenue(analyticsRepository.getCurrentMonthRevenue());
        summary.setMonthlyBookings(analyticsRepository.getCurrentMonthBookings());
        
        log.info("Dashboard summary retrieved successfully");
        return summary;
    }
}
