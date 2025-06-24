package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityRangeDTO {
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    // getters / setters
}