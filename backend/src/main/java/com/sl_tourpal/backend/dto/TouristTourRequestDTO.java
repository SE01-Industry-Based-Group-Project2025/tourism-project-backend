package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TouristTourRequestDTO {
    
    @NotBlank(message = "Tour name is required")
    private String name;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationValue;
    
    @NotBlank(message = "Duration unit is required")
    private String durationUnit = "days";
    
    @NotBlank(message = "Region is required")
    private String region;
    
    @NotEmpty(message = "At least one activity must be selected")
    private List<@NotBlank String> activities;
    
    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Group size is required")
    @Min(value = 1, message = "Group size must be at least 1")
    @Max(value = 50, message = "Group size cannot exceed 50")
    private Integer groupSize;
    
    @Size(max = 1000, message = "Special requirements cannot exceed 1000 characters")
    private String specialRequirements;
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
}