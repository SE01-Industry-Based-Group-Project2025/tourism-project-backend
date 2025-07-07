package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddTourRequest {
    // Basic Info
    @NotBlank private String name;
    @NotBlank private String category;
    @NotNull  private Integer durationValue;
    @NotBlank private String durationUnit;
    @Size(max = 500) private String shortDescription;
    @NotEmpty private List<@NotBlank String> highlights;
    @NotBlank private String difficulty;
    @NotBlank private String region;
    private Set<String> activities;

    // New fields for enhanced tour management
    private String status;
    private Boolean isCustom;
    private Integer availableSpots;

    // Single price instead of pricing tiers
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    // Itinerary
    private List<ItineraryDayDTO> itineraryDays;

    // Accommodation
    private List<AccommodationDTO> accommodations;

    // Availability
    private List<AvailabilityRangeDTO> availabilityRanges;

    // Media
    private List<TourImageDTO> images;

    // getters + setters omitted for brevity
}