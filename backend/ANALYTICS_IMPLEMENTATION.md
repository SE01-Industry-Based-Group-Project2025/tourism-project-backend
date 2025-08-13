# Analytics Module Implementation Guide

## Overview
This document describes the comprehensive analytics system implemented for the SL-Tourpal admin dashboard. The analytics module provides insights into revenue, bookings, tour performance, and customer behavior.

## Features Implemented

### 1. Dashboard Summary
**Endpoint:** `GET /api/admin/analytics/dashboard`
**Purpose:** Provides a comprehensive overview of key metrics

**Response Structure:**
```json
{
  "totalBookings": 150,
  "totalUsers": 89,
  "totalTours": 25,
  "totalRevenue": 125000.00,
  "activeBookings": 45,
  "completedBookings": 95,
  "cancelledBookings": 10,
  "monthlyRevenue": 15000.00,
  "monthlyBookings": 20
}
```

### 2. Monthly Revenue Analytics
**Endpoint:** `GET /api/admin/analytics/revenue/monthly?months=12`
**Purpose:** Shows revenue trends over time

**Parameters:**
- `months` (optional): Number of months to retrieve (1-24, default: 12)

**Response Structure:**
```json
[
  {
    "year": 2024,
    "month": 8,
    "monthName": "August",
    "revenue": 15000.00,
    "bookingCount": 20
  }
]
```

### 3. Tour Performance Analytics
**Endpoint:** `GET /api/admin/analytics/tours/performance?limit=10`
**Purpose:** Analyzes which tours are performing best by revenue and bookings

**Parameters:**
- `limit` (optional): Number of top tours to retrieve (1-50, default: 10)

**Response Structure:**
```json
[
  {
    "tourId": 1,
    "tourName": "Sigiriya Rock Fortress Tour",
    "category": "Cultural",
    "totalBookings": 45,
    "totalRevenue": 22500.00,
    "averageBookingValue": 500.00
  }
]
```

### 4. Customer Analysis
**Endpoint:** `GET /api/admin/analytics/customers/analysis?limit=20`
**Purpose:** Identifies top customers and their spending patterns

**Parameters:**
- `limit` (optional): Number of top customers to retrieve (1-100, default: 20)

**Customer Tier System:**
- **PLATINUM**: ≥ $10,000 total spent
- **GOLD**: $5,000 - $9,999 total spent
- **SILVER**: $2,000 - $4,999 total spent
- **BRONZE**: < $2,000 total spent

**Response Structure:**
```json
[
  {
    "userId": 123,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "totalBookings": 8,
    "totalSpent": 4000.00,
    "averageBookingValue": 500.00,
    "customerTier": "SILVER"
  }
]
```

### 5. Booking Trends
**Endpoint:** `GET /api/admin/analytics/bookings/trends?months=6`
**Purpose:** Shows booking volume trends and growth patterns

**Parameters:**
- `months` (optional): Number of months to analyze (1-24, default: 6)

**Trend Status:**
- **UP**: More bookings than previous month
- **DOWN**: Fewer bookings than previous month
- **STABLE**: Same number of bookings as previous month

**Response Structure:**
```json
[
  {
    "year": 2024,
    "month": 8,
    "monthName": "August",
    "bookingCount": 25,
    "averageBookingValue": 600.00,
    "trendStatus": "UP"
  }
]
```

## Implementation Details

### Database Queries
All analytics queries are optimized for performance:
- Use native SQL for complex aggregations
- Include proper indexes on date and status fields
- Filter out cancelled bookings for accurate revenue calculations
- Join with payment table to ensure only successful payments are counted

### Security
- All endpoints require `ADMIN` role authentication
- Uses `@PreAuthorize("hasRole('ADMIN')")` annotation
- Input validation with reasonable limits to prevent abuse

### Error Handling
- Parameter validation with default values for invalid inputs
- Comprehensive logging for debugging and monitoring
- Graceful handling of empty result sets

## Files Created/Modified

### DTOs (Data Transfer Objects)
1. `MonthlyRevenueDTO.java` - Monthly revenue data structure
2. `TourPerformanceDTO.java` - Tour performance metrics
3. `CustomerAnalysisDTO.java` - Customer spending analysis
4. `BookingTrendDTO.java` - Booking trend analysis
5. `DashboardSummaryDTO.java` - Dashboard overview metrics

### Repository Layer
1. `AnalyticsRepository.java` - Contains all analytics SQL queries

### Service Layer
1. `AnalyticsService.java` - Service interface
2. `AnalyticsServiceImpl.java` - Service implementation with business logic

### Controller Layer
1. `AnalyticsController.java` - REST API endpoints for analytics

## Usage Examples

### Frontend Integration
```javascript
// Get dashboard summary
const dashboardData = await fetch('/api/admin/analytics/dashboard', {
  headers: { 'Authorization': `Bearer ${adminToken}` }
});

// Get monthly revenue for last 6 months
const revenue = await fetch('/api/admin/analytics/revenue/monthly?months=6', {
  headers: { 'Authorization': `Bearer ${adminToken}` }
});

// Get top 15 performing tours
const topTours = await fetch('/api/admin/analytics/tours/performance?limit=15', {
  headers: { 'Authorization': `Bearer ${adminToken}` }
});
```

### Charts and Visualizations
The data structure is designed to work seamlessly with popular charting libraries:
- **Line Charts**: Monthly revenue and booking trends
- **Bar Charts**: Tour performance and customer analysis
- **Pie Charts**: Booking status distribution
- **Tables**: Detailed customer and tour analysis

## Performance Considerations

1. **Caching**: Consider implementing Redis cache for frequently accessed analytics
2. **Pagination**: Large datasets can be paginated using existing Spring Data patterns
3. **Indexing**: Ensure database indexes on `created_at`, `status`, and foreign key fields
4. **Background Jobs**: For real-time dashboards, consider background calculation of aggregates

## Future Enhancements

1. **Date Range Filters**: Add custom date range selection
2. **Export Functionality**: CSV/Excel export of analytics data
3. **Real-time Updates**: WebSocket integration for live dashboard updates
4. **Advanced Filtering**: Filter by tour category, customer tier, etc.
5. **Predictive Analytics**: Machine learning for demand forecasting
6. **Geographical Analytics**: Regional performance analysis

## Testing the Analytics Endpoints

Use tools like Postman or curl to test the endpoints:

```bash
# Get dashboard summary
curl -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
     http://localhost:8080/api/admin/analytics/dashboard

# Get monthly revenue
curl -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
     "http://localhost:8080/api/admin/analytics/revenue/monthly?months=12"

# Get top tours
curl -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
     "http://localhost:8080/api/admin/analytics/tours/performance?limit=10"
```

## Author
This analytics module was implemented as part of the SL-Tourpal backend system to provide comprehensive business intelligence capabilities for admin users.
