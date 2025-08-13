package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourAvailabilityRangeDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate date;
    private int capacity;
    private int booked;
    private int availableSpots;
    private int totalSpots;
    private boolean available;
    
    public TourAvailabilityRangeDTO(Long id, LocalDate date, int capacity, int booked) {
        this.id = id;
        this.date = date;
        this.capacity = capacity;
        this.booked = booked;
        this.availableSpots = capacity - booked;
        this.totalSpots = capacity;
        this.available = availableSpots > 0;
    }
}
