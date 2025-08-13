package com.sl_tourpal.backend.dto;

import java.time.LocalDate;

public class TourAvailabilityRangeDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer availableSpots;
    private Integer totalSpots;
    private Boolean isAvailable;
    
    public TourAvailabilityRangeDTO() {}
    
    public TourAvailabilityRangeDTO(LocalDate startDate, LocalDate endDate, Integer availableSpots) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableSpots = availableSpots;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getAvailableSpots() {
        return availableSpots;
    }
    
    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }
    
    public Integer getTotalSpots() {
        return totalSpots;
    }
    
    public void setTotalSpots(Integer totalSpots) {
        this.totalSpots = totalSpots;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}