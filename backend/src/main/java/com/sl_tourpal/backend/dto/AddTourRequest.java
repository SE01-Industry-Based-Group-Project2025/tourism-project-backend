package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddTourRequest {
    // Basic Info - Required fields for all tours (templates and non-templates)
    @NotBlank(message = "Tour name is required")
    private String name;
    
    @NotBlank(message = "Tour category is required")
    private String category;
    
    @Min(value = 1, message = "Duration value must be at least 1")
    private Integer durationValue;
    
    private String durationUnit;
    
    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    private String shortDescription;
    
    // Optional fields for templates, required for non-templates (validated in service layer)
    private List<String> highlights;
    private String difficulty;
    private String region;
    private Set<String> activities;

    // New fields for enhanced tour management
    private String status;
    private Boolean isCustom;
    private boolean isTemplate;
    private Integer availableSpots;

    // Optional fields for templates, required for non-templates (validated in service layer)
    private BigDecimal price;

    // Itinerary (optional for templates)
    private List<ItineraryDayDTO> itineraryDays;

    // Accommodation (optional for templates)
    private List<AccommodationDTO> accommodations;

    // Availability (optional for templates)
    private List<AvailabilityRangeDTO> availabilityRanges;

    // Media (optional for templates)
    private List<TourImageDTO> images;

    // Custom getter for primitive boolean isTemplate
    @JsonProperty("isTemplate")
    public boolean isTemplate() {
        return isTemplate;
    }
    
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    // getters + setters omitted for brevity
}