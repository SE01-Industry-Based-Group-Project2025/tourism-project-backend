package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    
    @NotNull(message = "Tour ID is required")
    private Long tourId;
    
    @NotNull(message = "Selected date is required")
    private LocalDate selectedDate;
    
    @Positive(message = "Guest count must be positive")
    private int guestCount;
    
    private String specialNote;
}
