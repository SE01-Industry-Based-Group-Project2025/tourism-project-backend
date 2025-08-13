package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.dto.*;

import java.util.List;

public interface AnalyticsService {
    List<MonthlyRevenueDTO> getMonthlyRevenue(int months);
    List<TourPerformanceDTO> getTourPerformance(int limit);
    List<CustomerAnalysisDTO> getCustomerAnalysis(int limit);
    List<BookingTrendDTO> getBookingTrends(int months);
    DashboardSummaryDTO getDashboardSummary();
}
