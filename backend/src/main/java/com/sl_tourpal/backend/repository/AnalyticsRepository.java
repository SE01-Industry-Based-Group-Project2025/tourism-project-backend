package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Booking, Long> {

    @Query(value = """
        SELECT 
            YEAR(b.created_at) as year,
            MONTH(b.created_at) as month,
            MONTHNAME(b.created_at) as monthName,
            COALESCE(SUM(p.amount), 0) as revenue,
            COUNT(b.id) as bookingCount
        FROM booking b
        LEFT JOIN payment p ON b.id = p.booking_id AND p.status = 'SUCCEEDED'
        WHERE b.status != 'CANCELLED'
        GROUP BY YEAR(b.created_at), MONTH(b.created_at)
        ORDER BY year DESC, month DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("limit") int limit);

    @Query(value = """
        SELECT 
            t.id as tourId,
            t.name as tourName,
            t.category as category,
            COUNT(b.id) as totalBookings,
            COALESCE(SUM(p.amount), 0) as totalRevenue,
            COALESCE(AVG(p.amount), 0) as averageBookingValue
        FROM tour t
        LEFT JOIN booking b ON t.id = b.tour_id AND b.status != 'CANCELLED'
        LEFT JOIN payment p ON b.id = p.booking_id AND p.status = 'SUCCEEDED'
        GROUP BY t.id, t.name, t.category
        HAVING COUNT(b.id) > 0
        ORDER BY totalRevenue DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getTourPerformance(@Param("limit") int limit);

    @Query(value = """
        SELECT 
            u.id as userId,
            u.first_name as firstName,
            u.last_name as lastName,
            u.email as email,
            COUNT(b.id) as totalBookings,
            COALESCE(SUM(p.amount), 0) as totalSpent,
            COALESCE(AVG(p.amount), 0) as averageBookingValue
        FROM user u
        LEFT JOIN booking b ON u.id = b.user_id AND b.status != 'CANCELLED'
        LEFT JOIN payment p ON b.id = p.booking_id AND p.status = 'SUCCEEDED'
        GROUP BY u.id, u.first_name, u.last_name, u.email
        HAVING COUNT(b.id) > 0
        ORDER BY totalSpent DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getCustomerAnalysis(@Param("limit") int limit);

    @Query(value = """
        SELECT 
            YEAR(b.created_at) as year,
            MONTH(b.created_at) as month,
            MONTHNAME(b.created_at) as monthName,
            COUNT(b.id) as bookingCount,
            COALESCE(AVG(p.amount), 0) as averageBookingValue
        FROM booking b
        LEFT JOIN payment p ON b.id = p.booking_id AND p.status = 'SUCCEEDED'
        WHERE b.status != 'CANCELLED'
        GROUP BY YEAR(b.created_at), MONTH(b.created_at)
        ORDER BY year DESC, month DESC
        LIMIT :months
        """, nativeQuery = true)
    List<Object[]> getBookingTrends(@Param("months") int months);

    @Query(value = "SELECT COUNT(*) FROM booking WHERE status != 'CANCELLED'", nativeQuery = true)
    Long getTotalBookings();

    @Query(value = "SELECT COUNT(*) FROM user", nativeQuery = true)
    Long getTotalUsers();

    @Query(value = "SELECT COUNT(*) FROM tour", nativeQuery = true)
    Long getTotalTours();

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payment WHERE status = 'SUCCEEDED'", nativeQuery = true)
    BigDecimal getTotalRevenue();

    @Query(value = "SELECT COUNT(*) FROM booking WHERE status = 'ACTIVE'", nativeQuery = true)
    Long getActiveBookings();

    @Query(value = "SELECT COUNT(*) FROM booking WHERE status = 'COMPLETED'", nativeQuery = true)
    Long getCompletedBookings();

    @Query(value = "SELECT COUNT(*) FROM booking WHERE status = 'CANCELLED'", nativeQuery = true)
    Long getCancelledBookings();

    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM booking b
        JOIN payment p ON b.id = p.booking_id
        WHERE p.status = 'SUCCEEDED'
        AND YEAR(b.created_at) = YEAR(CURRENT_DATE())
        AND MONTH(b.created_at) = MONTH(CURRENT_DATE())
        """, nativeQuery = true)
    BigDecimal getCurrentMonthRevenue();

    @Query(value = """
        SELECT COUNT(*)
        FROM booking b
        WHERE b.status != 'CANCELLED'
        AND YEAR(b.created_at) = YEAR(CURRENT_DATE())
        AND MONTH(b.created_at) = MONTH(CURRENT_DATE())
        """, nativeQuery = true)
    Long getCurrentMonthBookings();
}
